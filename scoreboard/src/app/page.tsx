"use client";
import { useImmer } from "use-immer";
import { ScoreboardData, SetTournamentMessage } from "@/interfaces";
import useWebSocket from "react-use-websocket";
import { Scoreboard } from "@/app/scoreboard";
import Dialog from "@mui/material/Dialog";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import DialogTitle from "@mui/material/DialogTitle";
import { useState } from "react";

function getWebsocketUrl(): string {
  let socketUrl;
  if (process.env.NODE_ENV == "production") {
    const location = window.location;
    if (location.protocol === "https:") {
      socketUrl = "wss:";
    } else {
      socketUrl = "ws:";
    }
    socketUrl += "//" + location.host + "/ws";
  } else {
    socketUrl = "ws://127.0.0.1:8080/ws";
  }
  return socketUrl;
}

export default function Page() {
  const [tournament, updateTournament] = useImmer<ScoreboardData | undefined>(
    undefined,
  );

  const [open, setOpen] = useState(false);

  const openDialog = () => {
    setOpen(true);
  };

  const closeDialog = () => {
    setOpen(false);
  };

  useWebSocket(getWebsocketUrl, {
    onOpen: () => {
      closeDialog();
      updateTournament(undefined);
    },
    // Will attempt to reconnect on all close events, such as server shutting down
    shouldReconnect: () => true,
    onMessage: (messageEvent) => handleMessage(messageEvent),
    onClose: openDialog,
  });

  function handleSetTournament(data: SetTournamentMessage) {
    updateTournament(data.data);
  }

  function handleMessage(messageEvent: WebSocketEventMap["message"]) {
    const data = JSON.parse(messageEvent.data);
    if (data.type === "set_tournament") {
      handleSetTournament(data);
    } else {
      console.warn("Unknown message type ", data.type);
    }
  }

  return (
    <>
      <Dialog open={open}>
        <DialogTitle>Keine Verbindung</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Keine Verbindung zur Turnierverwaltung
          </DialogContentText>
        </DialogContent>
      </Dialog>

      <Scoreboard tournament={tournament} />
    </>
  );
}
