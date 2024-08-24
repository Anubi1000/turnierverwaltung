import React, { useState, useCallback } from "react";
import { AppBar, Stack, Toolbar, Typography } from "@mui/material";
import Grid from "@mui/material/Unstable_Grid2";
import { DisciplineTable } from "@/app/disciplineTable";

function DisciplineTitleBar({
  disciplines,
  index,
}: {
  disciplines: { // all disciplines with tables
    label: string;
    table: React.ReactNode;
  }[];
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
              {disciplines[firstLabelIndex].label} {/*previous disclipline*/}
            </Typography>
          </Grid>

          <Grid>
            <Typography
              variant="h5"
              style={{ paddingLeft: 64, paddingRight: 64 }}
            >
              {disciplines[index].label} {/*current discipline*/}
            </Typography>
          </Grid>

          <Grid xs>
            <Typography variant="h6" color="lightGray">
              {disciplines[lastLabelIndex].label} {/*next disclipline*/}
            </Typography>
          </Grid>
        </Grid>
      </Toolbar>
    </AppBar>
  );
}

export function DisciplineCarousel() {
  const [index, setIndex] = useState(0);

  const nextDiscipline = useCallback(() => {
    setIndex((prevIndex) =>
      prevIndex >= disciplines.length - 1 ? 0 : prevIndex + 1,
    );
  }, []);

  function createRow(
    name: string,
    calories: string,
    fat: string,
    carbs: string,
    protein: string
  ) {
    return [name, calories, fat, carbs, protein];
  }

  function generateData() {
    var list = []
    for (var _i = 0; _i < 13; _i++) {
      list.push(createRow(_i.toString(), "159", "6.0", "24", "4.0"))
    }
    return list;
  }

  const rows = generateData()

  const columns = ["Dessert", "Calories", "Fat", "Carbs", "Protein"]

  const disciplines = [
    "Disziplin 1",
    "Disziplin 2",
    "Disziplin 3",
    "Disziplin 4",
  ].map((label) => ({
    label,
    table: <DisciplineTable moveNext={nextDiscipline} columns={columns} rows={rows}/>,
  }));

  return (
    <>
      <DisciplineTitleBar disciplines={disciplines} index={index} />
      {disciplines[index].table}
    </>
  );
}
