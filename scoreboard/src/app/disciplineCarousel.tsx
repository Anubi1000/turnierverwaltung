import React, { useCallback, useState } from "react";
import { AppBar, Stack, Toolbar, Typography } from "@mui/material";
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

  if (disciplines.length == 1) {
    return <></>;
  }

  if (disciplines.length == 2) {
    return (
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
              variant={index == 0 ? "h5" : "h6"}
              color={index == 0 ? "white" : "lightgray"}
              style={
                index == 0
                  ? {
                      paddingRight: 32,
                      textDecoration: "underline",
                    }
                  : {
                      paddingRight: 32,
                    }
              }
              align="right"
            >
              {disciplines[0]} {/*left discipline*/}
            </Typography>

            <Typography
              variant={index == 1 ? "h5" : "h6"}
              color={index == 1 ? "white" : "lightgray"}
              style={
                index == 1
                  ? {
                      paddingLeft: 32,
                      textDecoration: "underline",
                    }
                  : {
                      paddingLeft: 32,
                    }
              }
            >
              {disciplines[1]} {/*right discipline*/}
            </Typography>
          </div>
        </Toolbar>
      </AppBar>
    );
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
            {disciplines[previousLabelIndex]} {/*previous discipline*/}
          </Typography>

          <Typography
            variant="h5"
            style={{
              paddingLeft: 64,
              paddingRight: 64,
              textDecoration: "underline",
            }}
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
      prevIndex >= tables.length - 1 ? 0 : prevIndex + 1,
    );
  }, [tables.length]);

  if (tables.length == 0) {
    return (
      <Stack direction="column" justifyContent="center" sx={{ height: 1 }}>
        <Typography variant="h4" align="center">
          Keine Disziplinen vorhanden
        </Typography>
      </Stack>
    );
  }

  const disciplines = tables.map((table) => table.name);

  return (
    <>
      <DisciplineTitleBar disciplines={disciplines} index={index} />
      <DisciplineTable moveNext={nextDiscipline} table={tables[index]} />
    </>
  );
}
