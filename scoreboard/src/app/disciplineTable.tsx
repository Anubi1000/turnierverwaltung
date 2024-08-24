import * as React from "react";
import { useEffect, useRef, useState } from "react";
import { styled } from "@mui/material/styles";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";

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

function createData(
  name: string,
  calories: number,
  fat: number,
  carbs: number,
  protein: number,
) {
  return { name, calories, fat, carbs, protein };
}

const rows = [
  createData("Frozen yoghurt", 159, 6.0, 24, 4.0),
  createData("Ice cream sandwich", 237, 9.0, 37, 4.3),
  createData("Eclair", 262, 16.0, 24, 6.0),
  createData("Cupcake", 305, 3.7, 67, 4.3),
  createData("Gingerbread", 356, 16.0, 49, 3.9),
  createData("Eclair", 262, 16.0, 24, 6.0),
  createData("Cupcake", 305, 3.7, 67, 4.3),
  createData("Gingerbread", 356, 16.0, 49, 3.9),
  createData("Eclair", 262, 16.0, 24, 6.0),
  createData("Cupcake", 305, 3.7, 67, 4.3),
  createData("Gingerbread", 356, 16.0, 49, 3.9),
  createData("Eclair", 262, 16.0, 24, 6.0),
  createData("Cupcake", 305, 3.7, 67, 4.3),
  createData("Gingerbread", 356, 16.0, 49, 3.9),
];

export function DisciplineTable({ moveNext }: { moveNext: () => void }) {
  const tableRef = useRef<null | HTMLDivElement>(null);

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
        if (scrollCount >= 4) {
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
            <StyledTableCell>Dessert (100g serving)</StyledTableCell>
            <StyledTableCell align="right">Calories</StyledTableCell>
            <StyledTableCell align="right">Fat&nbsp;(g)</StyledTableCell>
            <StyledTableCell align="right">Carbs&nbsp;(g)</StyledTableCell>
            <StyledTableCell align="right">Protein&nbsp;(g)</StyledTableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((row) => (
            <StyledTableRow key={row.name}>
              <StyledTableCell component="th" scope="row">
                {row.name}
              </StyledTableCell>
              <StyledTableCell align="right">{row.calories}</StyledTableCell>
              <StyledTableCell align="right">{row.fat}</StyledTableCell>
              <StyledTableCell align="right">{row.carbs}</StyledTableCell>
              <StyledTableCell align="right">{row.protein}</StyledTableCell>
            </StyledTableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
