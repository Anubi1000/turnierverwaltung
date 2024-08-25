"use client";
import React, { useState } from "react";
import { AppBar, Stack, Toolbar, Typography } from "@mui/material";
import { Clock } from "@/app/clock";
import { DisciplineCarousel } from "@/app/discliplineCarousel";
import useWebSocket from "react-use-websocket";
import { SetTournamentMessage } from "./interfaces";
import { DisciplineDataTable } from "./interfaces";

const socketUrl = "ws://127.0.0.1:8080/ws";

export default function Page() {
  const [title, setTitle] = useState("");
  const [tables, setTables] = useState<DisciplineDataTable[]>([]);

  useWebSocket(socketUrl, {
    onOpen: () => console.log("opened"),
    //Will attempt to reconnect on all close events, such as server shutting down
    shouldReconnect: (closeEvent) => true,
    onMessage: (messageEvent) => handleMessage(messageEvent),
  });

  function handleMessage(messageEvent: WebSocketEventMap["message"]) {
    var data = JSON.parse(messageEvent.data);
    if (data.type == "set_tournament") {
      const message = data as SetTournamentMessage;
      const title = message.title;
      const tables = message.tables;
      setTitle(title);
      setTables(tables);
    } else {
      console.warn("Unknown message type ", data.type);
    }
  }
  return (
    <Stack direction="column" height="100vh">
      <AppBar position="static" elevation={0}>
        <Toolbar>
          <Stack
            direction="row"
            justifyContent="space-between"
            alignItems="center"
            sx={{ width: 1 }}
          >
            <Typography variant="h6">{title}</Typography>
            <Typography variant="h6">
              <Clock />
            </Typography>
          </Stack>
        </Toolbar>
      </AppBar>

      <DisciplineCarousel tables={tables} />
    </Stack>
  );
}
