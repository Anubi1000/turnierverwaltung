import { Tournament } from "@/app/interfaces";
import { AppBar, Stack, Toolbar, Typography } from "@mui/material";
import { Clock } from "@/app/clock";
import { DisciplineCarousel } from "@/app/disciplineCarousel";
import React from "react";

export function Scoreboard({ tournament }: { tournament?: Tournament }) {
  let content;

  if (!tournament) {
    content = (
      <Stack direction="column" justifyContent="center" sx={{ height: 1 }}>
        <Typography variant="h4" align="center">
          Kein Turnier ausgewählt
        </Typography>
      </Stack>
    );
  } else {
    content = <DisciplineCarousel tables={tournament.tables} />;
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
            <Typography id="TournamentTitle" variant="h6">
              {tournament ? tournament.title : ""}
            </Typography>
            <Typography id="Clock" variant="h6">
              <Clock />
            </Typography>
          </Stack>
        </Toolbar>
      </AppBar>
      {content}
    </Stack>
  );
}
