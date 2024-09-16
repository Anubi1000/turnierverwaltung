import * as React from "react";
import { useEffect, useRef, useState } from "react";
import { TournamentTable } from "@/app/interfaces";
import {
  Stack,
  styled,
  Table,
  TableBody,
  TableCell,
  tableCellClasses,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import LinearProgress from "@mui/material/LinearProgress";

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: theme.palette.primary.main, // header in green
    color: theme.palette.common.white,
  },
  [`&.${tableCellClasses.root}`]: {
    fontSize: 20,
    border: 0,
  },
}));

const StyledTableRow = styled(TableRow)(() => ({
  "&:nth-of-type(odd)": {
    backgroundColor: "#bbe0bb",
  },
  "&:nth-of-type(1)": {
    backgroundColor: "#ffd700", // gold
  },
  "&:nth-of-type(2)": {
    backgroundColor: "#d0d0d0", // silver
  },
  "&:nth-of-type(3)": {
    backgroundColor: "#bf8970", // bronze
  },
}));

export function DisciplineTable({
  moveNext,
  table,
}: {
  moveNext: () => void;
  table: TournamentTable;
}) {
  const tableRef = useRef<null | HTMLDivElement>(null);

  const [progress, setProgress] = useState(0);
  const [useProgress, setUseProgress] = useState(true);

  const rows = table.rows;
  const columns = table.columns;

  const maxScrolls = 4;
  const waitAtTopAndBottom = 1000;
  const scrollTimer = 25;
<<<<<<< HEAD
=======
  const pixelsBeforeUpdate = 10;

  var pixelsScrolledSinceLastUpdate = 0;
  var pixelsScrolled = 0;
>>>>>>> 48ece57 (improved scrolling and progress)

  useEffect(() => {
    let scrollToBottom = true;
    let isScrolling = true;
    let scrollCount = 0;
<<<<<<< HEAD
    let pixelsScrolled = 0;
=======
>>>>>>> 48ece57 (improved scrolling and progress)

    const interval = setInterval(async () => {
      if (!isScrolling) return;

      const tableContainer = tableRef.current as unknown as HTMLDivElement;

      if (tableContainer) {
<<<<<<< HEAD
        const totalScrollDist =
          (tableContainer.scrollHeight - tableContainer.offsetHeight) *
          maxScrolls;

=======
>>>>>>> 48ece57 (improved scrolling and progress)
        // ensure ref object exists

        const totalScrollDist =
          (tableContainer.scrollHeight - tableContainer.offsetHeight) *
          maxScrolls;

        if (tableContainer.offsetHeight < tableContainer.scrollHeight) {
          setUseProgress(true);
        } else {
          setUseProgress(false);
          setProgress(100);
        }
        if (scrollToBottom) {
          tableContainer.scrollTop += 1; // one pixel down
<<<<<<< HEAD
          pixelsScrolled++;
          setProgress((100 / totalScrollDist) * pixelsScrolled);
        } else {
          tableContainer.scrollTop -= 1; // one pixel up
          pixelsScrolled++;
          setProgress((100 / totalScrollDist) * pixelsScrolled);
        }

=======
        } else {
          tableContainer.scrollTop -= 1; // one pixel up
        }

        if (useProgress) {
          pixelsScrolled++;
          pixelsScrolledSinceLastUpdate++;
          if (pixelsScrolledSinceLastUpdate >= pixelsBeforeUpdate) {
            pixelsScrolledSinceLastUpdate = 0;
            setProgress((100 / totalScrollDist) * pixelsScrolled);
          }
        }

>>>>>>> 48ece57 (improved scrolling and progress)
        if (tableContainer.scrollTop == 0 && !scrollToBottom) {
          // at top of table
          isScrolling = false;
          setTimeout(() => {
            scrollToBottom = true;
            isScrolling = true;
            scrollCount++;
            if (useProgress) {
              setProgress((100 / totalScrollDist) * pixelsScrolled);
            }
          }, waitAtTopAndBottom); // wait five seconds then scroll down
        } else if (
          tableContainer.scrollTop + tableContainer.clientHeight ==
            tableContainer.scrollHeight &&
          scrollToBottom // reached bottom
        ) {
          isScrolling = false;
          setTimeout(() => {
            scrollToBottom = false;
            isScrolling = true;
            scrollCount++;
            if (useProgress) {
              setProgress((100 / totalScrollDist) * pixelsScrolled);
            }
          }, waitAtTopAndBottom); // wait five seconds then scroll up
        }
        if (scrollCount >= maxScrolls) {
          scrollCount = 0;
          if (useProgress) {
            setProgress(0);
            pixelsScrolled = 0;
          }
          moveNext();
        }
      }
    }, scrollTimer);
    return () => {
      clearInterval(interval);
    };
  }, [tableRef, table, useProgress]);

  if (columns.length == 0) {
    return (
      <TableContainer ref={tableRef} sx={{ height: 1 }}>
        <Stack direction="column" justifyContent="center" sx={{ height: 1 }}>
          <Typography variant="h4" align="center">
            Keine Spaltendefinition vorhanden
          </Typography>
        </Stack>
      </TableContainer>
    );
  }

  if (rows.length == 0) {
    return (
      <TableContainer ref={tableRef} sx={{ height: 1 }}>
        <Stack direction="column" justifyContent="center" sx={{ height: 1 }}>
          <Typography variant="h4" align="center">
            Keine Einträge vorhanden
          </Typography>
        </Stack>
      </TableContainer>
    );
  }

  return (
    <>
      <LinearProgress
        variant="determinate"
        value={progress}
        style={{ zIndex: 1 }}
      />
      <TableContainer ref={tableRef} style={{ overflowY: "hidden" }}>
        <Table stickyHeader>
          <TableHead>
            <TableRow>
              <StyledTableCell
                key={-1}
                style={{ width: "60px" }}
                align={"center"}
              >
                Platz
              </StyledTableCell>
              {columns.map((column, index) => (
                <StyledTableCell
                  key={`col-${index}`}
                  style={{ width: column.width }}
                  align={column.alignment}
                  data-testid={`col-${index}`}
                >
                  {column.name}
                </StyledTableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {rows.map((row, rowIndex) => (
              <StyledTableRow key={row.id} data-testid={`row-${row.id}`}>
                <StyledTableCell key={`place-${rowIndex + 1}`} align={"center"}>
                  <b>{rowIndex + 1}</b>
                </StyledTableCell>
                {row.values.map((entry, index) => (
                  <StyledTableCell
                    key={`cell-${row.id}-${index}`}
                    align={columns[index].alignment}
                    data-testid={`cell-${row.id}-${index}`}
                  >
                    {entry}
                  </StyledTableCell>
                ))}
              </StyledTableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </>
  );
}
