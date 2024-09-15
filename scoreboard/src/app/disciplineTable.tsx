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
import LinearProgress from '@mui/material/LinearProgress';

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

const StyledTableRow = styled(TableRow)(({ theme }) => ({
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

  const rows = table.rows;
  const columns = table.columns;
  const maxScrolls = 2;
  const waitAtTopAndBottom = 5000;
  const scrollTimer = 25;
  var pixelsScrolled = 0;

  useEffect(() => {
    let scrollToBottom = true;
    let isScrolling = true;
    let scrollCount = 0;
    
    
    const interval = setInterval(async () => {
      if (!isScrolling) return;

      const tableContainer = tableRef.current as unknown as HTMLDivElement;

      if (tableContainer) { 

        const totalScrollDist = (tableContainer.scrollHeight-tableContainer.offsetHeight)*maxScrolls;

        // ensure ref object exists
        if (scrollToBottom) {
          tableContainer.scrollTop += 1; // one pixel down
          pixelsScrolled++;
          setProgress((100/totalScrollDist)*pixelsScrolled);
        } else {
          tableContainer.scrollTop -= 1; // one pixel up
          pixelsScrolled++;
          setProgress((100/totalScrollDist)*pixelsScrolled);
        }
       
        if (tableContainer.scrollTop == 0 && !scrollToBottom) {
          // at top of table
          isScrolling = false;
          setTimeout(() => {
            scrollToBottom = true;
            isScrolling = true;
            scrollCount++;
            setProgress((100/totalScrollDist)*pixelsScrolled);
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
            setProgress((100/totalScrollDist)*pixelsScrolled);
          }, waitAtTopAndBottom); // wait five seconds then scroll up
        }
        if (scrollCount >= maxScrolls) {
          scrollCount = 0;
          setProgress(0)
          pixelsScrolled = 0;
          moveNext();
        }
      }
    }, scrollTimer);
    return () => {
      clearInterval(interval);
    };
  }, [tableRef]);

  if (columns.length == 0) {
    return (
      <>
      <LinearProgress variant="determinate" value={progress} />
      <TableContainer ref={tableRef} sx={{ height: 1 }}>
        <Stack direction="column" justifyContent="center" sx={{ height: 1 }}>
          <Typography variant="h4" align="center">
            Keine Spaltendefinition vorhanden
          </Typography>
        </Stack>
      </TableContainer>
      </>
    );
  }

  if (rows.length == 0) {
    return (
      <>
      <LinearProgress variant="determinate" value={progress} />
      <TableContainer ref={tableRef} sx={{ height: 1 }}>    
        <Stack direction="column" justifyContent="center" sx={{ height: 1 }}>
          <Typography variant="h4" align="center">
            Keine Einträge vorhanden
          </Typography>
        </Stack>
      </TableContainer>
      </>   
    );
  }

  return (
    <>
    <LinearProgress variant="determinate" value={progress} />
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
