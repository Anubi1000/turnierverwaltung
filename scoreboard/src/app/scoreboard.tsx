import { ScoreboardData } from "@/interfaces";
import { AppBar, Stack, Toolbar, Typography } from "@mui/material";
import { Clock } from "@/app/clock";
import { DisciplineCarousel } from "@/app/disciplineCarousel";
import Image from "next/image";
import { CenteredText } from "@/app/centeredText";

const imageSize = 80;

export function Scoreboard({ tournament }: { tournament?: ScoreboardData }) {
  let content;

  if (!tournament) {
    content = <CenteredText text="Kein Turnier ausgewählt" />;
  } else {
    content = <DisciplineCarousel tables={tournament.tables} />;
  }

  return (
    <Stack direction="column" height="100vh">
      <AppBar position="static" elevation={0}>
        <Toolbar>
          <Stack direction="row" alignItems="center" sx={{ width: 1 }}>
            <Image
              src={"/logo.png"}
              alt={"Logo"}
              width={imageSize}
              height={imageSize}
            />
            <Typography id="TournamentTitle" variant="h5" marginLeft={4}>
              {tournament ? tournament.name : ""}
            </Typography>

            <Typography id="Clock" variant="h5" marginLeft="auto">
              <Clock />
            </Typography>
          </Stack>
        </Toolbar>
      </AppBar>

      {content}
    </Stack>
  );
}
