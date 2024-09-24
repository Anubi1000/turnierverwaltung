import { CSSProperties, useRef } from "react";
import { CenteredText } from "@/app/centeredText";
import {
  checkIfColIsFixed,
  ScoreboardData_Table,
  ScoreboardData_Table_Column,
  ScoreboardData_Table_Row,
} from "@/interfaces";
import {
  LinearProgress,
  linearProgressClasses,
  styled,
  Table,
  TableBody,
  TableCell,
  tableCellClasses,
  TableContainer,
  TableHead,
  TableRow,
} from "@mui/material";
import { useAutoscroll } from "@/app/useAutoscroll";

const StyledLinearProgress = styled(LinearProgress)({
  [`& > .${linearProgressClasses.bar}`]: {
    transition: "transform .05s linear",
  },
});

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: theme.palette.primary.main,
    color: theme.palette.common.white,
  },
  [`&.${tableCellClasses.root}`]: {
    fontSize: 20,
    border: 0,
  },
}));

const StyledTableRow = styled(TableRow)(() => ({
  "&:nth-of-type(even)": {
    backgroundColor: "var(--mui-palette-LinearProgress-primaryBg)",
  },
}));

export function DisciplineTable({
  table,
  moveNext,
}: {
  table: ScoreboardData_Table;
  moveNext: () => void;
}) {
  const containerRef = useRef<HTMLDivElement>(null);

  const { progress } = useAutoscroll({
    containerRef: containerRef,
    msPerPixel: 50,
    waitTime: 5000,
    numOfScrolls: 4,
    onFinish: moveNext,
  });

  const rows = table.rows;
  const columns = table.columns;

  if (columns.length == 0) {
    return (
      <>
        <StyledLinearProgress value={progress * 100} variant="determinate" />
        <div ref={containerRef} />
        <CenteredText text="Keine Spaltendefinition vorhanden" />
      </>
    );
  }

  if (rows.length == 0) {
    return (
      <>
        <StyledLinearProgress value={progress * 100} variant="determinate" />
        <div ref={containerRef} />
        <CenteredText text="Keine Einträge vorhanden" />
      </>
    );
  }

  return (
    <>
      <StyledLinearProgress value={progress * 100} variant="determinate" />
      <TableContainer ref={containerRef} style={{ overflowY: "scroll" }}>
        <Table stickyHeader>
          <Header columns={columns} />
          <Body rows={rows} columns={columns} />
        </Table>
      </TableContainer>
    </>
  );
}

function getColumnStyle(
  width: { width: number } | { weight: number },
): CSSProperties {
  if (checkIfColIsFixed(width)) {
    return { width: width.width };
  } else {
    return { flexGrow: width.weight };
  }
}

function Header({ columns }: { columns: ScoreboardData_Table_Column[] }) {
  const elements = columns.map((col, index) => (
    <StyledTableCell
      key={`col-${index}`}
      align={col.alignment}
      style={getColumnStyle(col.width)}
      data-testid={`col-${index}`}
    >
      {col.name}
    </StyledTableCell>
  ));

  return (
    <TableHead>
      <TableRow>{elements}</TableRow>
    </TableHead>
  );
}

function Body({
  rows,
  columns,
}: {
  rows: ScoreboardData_Table_Row[];
  columns: ScoreboardData_Table_Column[];
}) {
  const rowElements = rows.map((row, rowIndex) => {
    const elements = row.values.map((value, index) => (
      <StyledTableCell
        key={`cell-${rowIndex}-${index}`}
        align={columns[index].alignment}
        style={getColumnStyle(columns[index].width)}
        data-testid={`cell-${rowIndex}-${index}`}
      >
        {value}
      </StyledTableCell>
    ));

    return <StyledTableRow key={`row-${rowIndex}`} data-testid={`row-${rowIndex}`}>{elements}</StyledTableRow>;
  });

  return <TableBody>{rowElements}</TableBody>;
}
