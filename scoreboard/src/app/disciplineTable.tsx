import * as React from "react";
import { useEffect, useRef } from "react";
import { TournamentTable } from "@/app/interfaces";
import {
  styled,
  Table,
  TableBody,
  TableCell,
  tableCellClasses,
  TableContainer,
  TableHead,
  TableRow,
  Stack,
  Typography,
} from "@mui/material";

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

  const rows = table.rows;
  const columns = table.columns;

  useEffect(() => {
    let scrollToBottom = true;
    let isScrolling = true;
    let scrollCount = 0;

    const interval = setInterval(async () => {
      if (!isScrolling) return;

      const tableContainer = tableRef.current as unknown as HTMLDivElement;

      if (tableContainer) {
        // ensure ref object exists
        if (scrollToBottom) {
          tableContainer.scrollTop += 1; // one pixel down
        } else {
          tableContainer.scrollTop -= 1; // one pixel up
        }
        if (tableContainer.scrollTop == 0 && !scrollToBottom) {
          // at top of table
          isScrolling = false;
          setTimeout(() => {
            scrollToBottom = true;
            isScrolling = true;
            scrollCount++;
          }, 5000); // wait five seconds then scroll down
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
          }, 5000); // wait five seconds then scroll up
        }
        if (scrollCount >= 2) {
          scrollCount = 0;
          moveNext();
        }
      }
    }, 25);

    return () => {
      clearInterval(interval);
    };
  }, []);

  if (columns.length == 0) {
    return (
      <Stack direction="column" justifyContent="center" sx={{ height: 1 }}>
        <Typography variant="h4" align="center">
          Keine Spaltendefinition vorhanden
        </Typography>
      </Stack>
    );
  }

  if (rows.length == 0) {
    return (
      <Stack direction="column" justifyContent="center" sx={{ height: 1 }}>
        <Typography variant="h4" align="center">
          Keine Einträge vorhanden
        </Typography>
      </Stack>
    );
  }

  return (
    <TableContainer ref={tableRef} style={{ overflowY: "scroll" }}>
      <Table stickyHeader>
        <TableHead>
          <TableRow>
            {columns.map((column) => (
              <StyledTableCell
                key={column.name}
                style={{ width: column.width }}
                align={column.alignment}
              >
                {column.name}
              </StyledTableCell>
            ))}
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((row) => (
            <StyledTableRow key={row.id}>
              {row.values.map((entry, index) => (
                <StyledTableCell key={index} align={columns[index].alignment}>
                  {entry}
                </StyledTableCell>
              ))}
            </StyledTableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
