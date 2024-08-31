"use client";
import React from "react";
import useWebSocket from "react-use-websocket";
import { SetTournamentMessage, Tournament } from "@/app/interfaces";
import { Scoreboard } from "@/app/scoreboard";
import { useImmer } from "use-immer";

const socketUrl = "ws://127.0.0.1:8080/ws";

export default function Page() {
  const [tournament, updateTournament] = useImmer<Tournament | undefined>(
    undefined,
  );

  useWebSocket(socketUrl, {
    onOpen: () => console.log("opened"),
    // Will attempt to reconnect on all close events, such as server shutting down
    shouldReconnect: (closeEvent) => true,
    onMessage: (messageEvent) => handleMessage(messageEvent),
  });

  function handleMessage(messageEvent: WebSocketEventMap["message"]) {
    const data = JSON.parse(messageEvent.data);
    if (data.type == "set_tournament") {
      const message = data as SetTournamentMessage;
      updateTournament({
        title: message.title,
        tables: message.tables,
      });
    } else {
      console.warn("Unknown message type ", data.type);
    }
  }
  return <Scoreboard tournament={tournament} />;
}
