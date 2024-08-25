import * as React from "react";
import { useEffect, useRef, useState } from "react";
import { styled } from "@mui/material/styles";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import { Row, Column, DisciplineDataTable } from "./interfaces";

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: theme.palette.primary.main, // header in green
    color: theme.palette.common.white,
    fontSize: 20,
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 20,
  },
}));

const StyledTableRow = styled(TableRow)(({ theme }) => ({
  "&:nth-of-type(even)": {
    backgroundColor: "#ffffff",
  },
  "&:nth-of-type(odd)": {
    backgroundColor: "#90ee90",
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
  // hide last border
  "&:last-child td, &:last-child th": {
    border: 0,
  },
}));

export function DisciplineTable({
  moveNext,
  table,
}: {
  moveNext: () => void;
  table: DisciplineDataTable;
}) {
  const tableRef = useRef<null | HTMLDivElement>(null);

  const rows = table.rows
  const columns = table.columns

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

  return (
    <TableContainer ref={tableRef} style={{ overflowY: "hidden" }}>
      <Table stickyHeader>
        <TableHead>
          <TableRow>
            {columns.map((column, index) =>
              index == 0 ? (
                <StyledTableCell
                  key={column.name}
                  style={{ width: column.width }}
                >
                  {column.name}
                </StyledTableCell>
              ) : (
                <StyledTableCell
                  key={column.name}
                  style={{ width: column.width }}
                  align="right"
                >
                  {column.name}
                </StyledTableCell>
              ),
            )}
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((row, index) => (
            <StyledTableRow key={index}>
              {row.values.map((entry, index) =>
                index == 0 ? (
                  <StyledTableCell key={index} component="th" scope="row">
                    {entry}
                  </StyledTableCell>
                ) : (
                  <StyledTableCell key={index} align="right">
                    {entry}
                  </StyledTableCell>
                ),
              )}
            </StyledTableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
