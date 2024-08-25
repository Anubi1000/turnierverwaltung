import React, { useState, useCallback } from "react";
import { AppBar, Stack, Toolbar, Typography } from "@mui/material";
import Grid from "@mui/material/Unstable_Grid2";
import { DisciplineTable } from "@/app/disciplineTable";
import { Row, Column, DisciplineDataTable } from "./interfaces";

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
        <Grid container spacing={0} style={{ width: "100%" }}>
          <Grid xs>
            <Typography variant="h6" color="lightGray" align="right">
              {disciplines[firstLabelIndex]} {/*previous disclipline*/}
            </Typography>
          </Grid>

          <Grid>
            <Typography
              variant="h5"
              style={{ paddingLeft: 64, paddingRight: 64 }}
            >
              {disciplines[index]} {/*current discipline*/}
            </Typography>
          </Grid>

          <Grid xs>
            <Typography variant="h6" color="lightGray">
              {disciplines[lastLabelIndex]} {/*next disclipline*/}
            </Typography>
          </Grid>
        </Grid>
      </Toolbar>
    </AppBar>
  );
}

export function DisciplineCarousel({
  tables,
}: {
  tables: DisciplineDataTable[];
}) {
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
