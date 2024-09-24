import { useCallback, useState } from "react";
import { AppBar, Toolbar, Typography, useTheme } from "@mui/material";
import { ScoreboardData_Table } from "@/interfaces";
import { CenteredText } from "@/app/centeredText";
import { DisciplineTable } from "@/app/disciplineTable";

function DisciplineTitleBar({
  disciplines,
  index,
}: {
  disciplines: string[];
  index: number;
}) {
  const theme = useTheme();

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

  let content;
  if (disciplines.length == 2) {
    content = <div
      style={{
        width: "100%",
        display: "grid",
        gridTemplateColumns: "1fr 1fr",
        backgroundColor: theme.palette.primary.main,
        height: 48
      }}
    >
      <Typography
        variant={index == 0 ? "h5" : "h6"}
        color={index == 0 ? "white" : "lightgray"}
        style={
          index == 0
            ? {
              paddingRight: 32,
              textDecoration: "underline"
            }
            : {
              paddingRight: 32
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
              textDecoration: "underline"
            }
            : {
              paddingLeft: 32
            }
        }
      >
        {disciplines[1]} {/*right discipline*/}
      </Typography>
    </div>;
  } else {
    content = <div
      style={{
        width: "100%",
        display: "grid",
        gridTemplateColumns: "1fr auto 1fr"
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
          textDecoration: "underline"
        }}
        align="center"
      >
        {disciplines[index]} {/*current discipline*/}
      </Typography>

      <Typography variant="h6" color="lightGray">
        {disciplines[nextLabelIndex]} {/*next discipline*/}
      </Typography>
    </div>;
  }

  return (
    <AppBar position="static" elevation={0}>
      <Toolbar>
        {content}
      </Toolbar>
    </AppBar>
  );
}

export function DisciplineCarousel({
  tables
}: {
  tables: ScoreboardData_Table[];
}) {
  const [index, setIndex] = useState(0);

  const nextDiscipline = useCallback(() => {
    setIndex((prevIndex) =>
      prevIndex >= tables.length - 1 ? 0 : prevIndex + 1
    );
  }, [tables.length]);

  if (tables.length == 0) {
    return <CenteredText text="Keine Disziplinen vorhanden" />;
  }

  const disciplines = tables.map((table) => table.name);

  return (
    <>
      <DisciplineTitleBar disciplines={disciplines} index={index} />
      <DisciplineTable
        table={tables[index]}
        moveNext={nextDiscipline}
        key={index}
      />
    </>
  );
}
