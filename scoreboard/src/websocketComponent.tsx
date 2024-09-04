"use client";
import React from "react";
import { useEffect } from "react";
import useWebSocket from "react-use-websocket";
import {
  SetTournamentMessage,
  Tournament,
  TournamentTable,
  TournamentTableRow,
  UpdateRowMessage,
} from "@/app/interfaces";
import { Scoreboard } from "@/app/scoreboard";
import { useImmer } from "use-immer";

const socketUrl = "ws://127.0.0.1:8080/ws";

function compareRows(
  firstRow: TournamentTableRow,
  secondRow: TournamentTableRow,
) {
  for (let index = 0; index < firstRow.sortValues.length; index++) {
    const firstValue = firstRow.sortValues[index];
    const secondValue = secondRow.sortValues[index];
    const result = firstValue - secondValue;
    if (result != 0) {
      return result;
    }
  }
  return 0;
}

function sortTables(tables: TournamentTable[]) {
  tables.forEach((table: TournamentTable) => {
    table.rows.sort((firstRow, secondRow) => {
      return compareRows(firstRow, secondRow);
    });
  });
}

export default function WebSocketComponent({tournamentProp}:{tournamentProp: Tournament | null
}) {

  const [tournament, updateTournament] = useImmer<Tournament | undefined>(
    undefined,
  );

  const webSocket = useWebSocket(socketUrl, {
    onOpen: () => console.log("opened"),
    // Will attempt to reconnect on all close events, such as server shutting down
    shouldReconnect: (closeEvent) => true,
    onMessage: (messageEvent) => handleMessage(messageEvent),
  });

  function handleSetTournament(data: SetTournamentMessage) {
    const tables = data.tables;
    sortTables(tables);
    updateTournament({
      title: data.title,
      tables: data.tables,
    });
  }

  function handleUpdateTournament(data: UpdateRowMessage) {
    if (!tournament) {
      webSocket.sendJsonMessage({ type: "resend_tournament" });
      return;
    }
    const message = data as UpdateRowMessage;
    const tableIndex = tournament.tables.findIndex((table: TournamentTable) => {
      return table.id === message.tableId;
    });

    if (tableIndex === -1) {
      webSocket.sendJsonMessage({ type: "resend_tournament" });
      return;
    }

    const table = tournament.tables[tableIndex];
    const rowIndex = table.rows.findIndex((row: TournamentTableRow) => {
      return row.id === message.rowId;
    });

    updateTournament((draft) => {
      if (!draft) return;
      if (rowIndex === -1) {
        draft.tables[tableIndex].rows.push({
          id: message.rowId,
          values: message.values,
          sortValues: message.sortValues,
        });
      } else {
        draft.tables[tableIndex].rows[rowIndex] = {
          id: message.rowId,
          values: message.values,
          sortValues: message.sortValues,
        };
      }
      sortTables(draft.tables);
    });
  }

  function handleMessage(messageEvent: WebSocketEventMap["message"]) {
    const data = JSON.parse(messageEvent.data);
    if (data.type === "set_tournament") {
      handleSetTournament(data);
    } else if (data.type === "update_row") {
      handleUpdateTournament(data);
    } else {
      console.warn("Unknown message type ", data.type);
    }
  }

  useEffect(() => {
    if (tournamentProp) {
      updateTournament(tournamentProp)
    }
  }, []);

  return <Scoreboard tournament={tournament} />;
}
