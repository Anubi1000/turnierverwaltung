import React, { useState, useCallback } from "react";
import { AppBar, Toolbar, Typography } from "@mui/material";
import { DisciplineTable } from "@/app/disciplineTable";
import { TournamentTable } from "@/app/interfaces";

function DisciplineTitleBar({
  disciplines,
  index,
}: {
  disciplines: string[];
  index: number;
}) {
  let previousLabelIndex = index - 1;
  if (previousLabelIndex == -1) {
    previousLabelIndex = disciplines.length - 1;
  }

  let nextLabelIndex = index + 1;
  if (nextLabelIndex == disciplines.length) {
    nextLabelIndex = 0;
  }

  return disciplines.length == 1 ? (
    <></>
  ) : disciplines.length == 2 ? (
    <AppBar position="static" elevation={0}>
      <Toolbar>
        <div
          style={{
            width: "100%",
            display: "grid",
            gridTemplateColumns: "1fr 1fr",
          }}
        >
          <Typography
            variant="h5"
            color={index == 0 ? "white" : "lightgray"}
            style={{ paddingRight: 32 }}
          >
            {disciplines[0]} {/*left discipline*/}
          </Typography>

          <Typography
            variant="h5"
            color={index == 1 ? "white" : "lightgray"}
            style={{ paddingLeft: 32 }}
          >
            {disciplines[1]} {/*right discipline*/}
          </Typography>
        </div>
      </Toolbar>
    </AppBar>
  ) : (
    <AppBar position="static" elevation={0}>
      <Toolbar>
        <div
          style={{
            width: "100%",
            display: "grid",
            gridTemplateColumns: "1fr auto 1fr",
          }}
        >
          <Typography variant="h6" color="lightGray" align="right">
            {disciplines[previousLabelIndex]} {/*previous discipline*/}
          </Typography>

          <Typography
            variant="h5"
            style={{ paddingLeft: 64, paddingRight: 64 }}
            align="center"
          >
            {disciplines[index]} {/*current discipline*/}
          </Typography>

          <Typography variant="h6" color="lightGray">
            {disciplines[nextLabelIndex]} {/*next discipline*/}
          </Typography>
        </div>
      </Toolbar>
    </AppBar>
  );
}
export function DisciplineCarousel({ tables }: { tables: TournamentTable[] }) {
  const [index, setIndex] = useState(0);

  const nextDiscipline = useCallback(() => {
    setIndex((prevIndex) =>
      prevIndex >= disciplines.length - 1 ? 0 : prevIndex + 1,
    );
  }, []);

  const disciplines = tables.map((table) => table.name);

  return (
    <>
      <DisciplineTitleBar disciplines={disciplines} index={index} />
      <DisciplineTable moveNext={nextDiscipline} table={tables[index]} />
    </>
  );
}
