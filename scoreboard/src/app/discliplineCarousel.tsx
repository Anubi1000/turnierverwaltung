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
  let firstLabelIndex = index - 1;
  if (firstLabelIndex == -1) {
    firstLabelIndex = disciplines.length - 1;
  }

  let lastLabelIndex = index + 1;
  if (lastLabelIndex == disciplines.length) {
    lastLabelIndex = 0;
  }

  return (
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
            {disciplines[firstLabelIndex]} {/*previous discipline*/}
          </Typography>

          <Typography
            variant="h5"
            style={{ paddingLeft: 64, paddingRight: 64 }}
            align="center"
          >
            {disciplines[index]} {/*current discipline*/}
          </Typography>

          <Typography variant="h6" color="lightGray">
            {disciplines[lastLabelIndex]} {/*next discipline*/}
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
