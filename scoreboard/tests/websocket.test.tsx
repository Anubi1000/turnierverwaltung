import { afterEach, beforeEach, describe, expect, it } from "@jest/globals";
import Page from "@/app/page";
import { render, screen } from "@testing-library/react";
import { act } from "react";
import "@testing-library/jest-dom/jest-globals";
import "@testing-library/jest-dom";
import WS from "jest-websocket-mock";
import {
  SetTournamentMessage,
  TournamentTable,
  UpdateRowMessage,
} from "@/app/interfaces";

function createDummyTable() {
  const table: TournamentTable = {
    id: "Table",
    name: "Discipline",
    columns: [],
    rows: [],
  };
  for (let i = 0; i < 5; i++) {
    table.columns.push({
      name: `Col ${i}`,
      alignment: "center",
      width: "20%",
    });
  }
  for (let i = 0; i < 10; i++) {
    table.rows.push({
      values: ["2", "4", "6", "8", "10"],
      id: i.toString(),
      sortValues: [i],
    });
  }
  return table;
}

describe("WebSocket", () => {
  let server: WS;

  beforeEach(() => {
    server = new WS("ws://127.0.0.1:8080/ws", { jsonProtocol: true });
  });

  afterEach(() => {
    WS.clean();
  });

  it("check if tournament is set", async () => {
    render(<Page />);
    await server.connected;

    const table = createDummyTable();

    const message: SetTournamentMessage = {
      tables: [table],
      title: "TestTournament",
      type: "set_tournament",
    };

    act(() => {
      server.send(message);
    });

    const title = screen.getByText(message.title);
    expect(title).toBeInTheDocument();

    // ensure column headers are set
    table.columns.forEach((col, index) => {
      const element = screen.getByTestId(`col-${index}`);
      expect(element).toBeInTheDocument();
      expect(element.textContent).toBe(col.name);
    });

    table.rows.forEach((row, index) => {
      const element = screen.getByTestId(`row-${row.id}`);
      expect(element).toBeInTheDocument();
      row.values.forEach((value, index) => {
        const cell = screen.getByTestId(`cell-${row.id}-${index}`);
        expect(cell).toBeInTheDocument();
        expect(cell.textContent).toBe(value);
      });
    });
  });

  it("check if updates are done correctly", async () => {
    // how to pass inital tournament to scoreboard?
    const table = createDummyTable();
    const initMessage: SetTournamentMessage = {
      tables: [table],
      title: "TestTournament",
      type: "set_tournament",
    };

    render(<Page />);

    await server.connected;

    act(() => {
      server.send(initMessage);
    });

    // apply update
    const updateMessage: UpdateRowMessage = {
      tableId: "Table",
      rowId: "1",
      values: ["1", "3", "5", "7", "11"],
      sortValues: [1, 2, 3, 4, 5],
      type: "update_row",
    };

    act(() => {
      server.send(updateMessage);
    });

    // ensure column headers are set
    table.columns.forEach((col, index) => {
      const element = screen.getByTestId(`col-${index}`);
      expect(element).toBeInTheDocument();
      expect(element.textContent).toBe(col.name);
    });

    // ensure first row is updated to match new values
    const row = table.rows[0];
    const rowElement = screen.getByTestId(`row-${row.id}`);
    expect(rowElement).toBeInTheDocument();
    row.values.forEach((value, index) => {
      const cell = screen.getByTestId(`cell-${row.id}-${index}`);
      expect(cell).toBeInTheDocument();
      expect(cell.textContent).toBe(value);
    });
  });

  it("check if new rows are inserted correctly", async () => {
    // how to pass inital tournament to scoreboard?
    const table = createDummyTable();
    const initMessage: SetTournamentMessage = {
      tables: [table],
      title: "TestTournament",
      type: "set_tournament",
    };

    render(<Page />);

    await server.connected;

    act(() => {
      server.send(initMessage);
    });

    // apply update
    const newRowId = "42";
    const updateMessage: UpdateRowMessage = {
      tableId: "Table",
      rowId: newRowId,
      values: ["1", "3", "5", "7", "11"],
      sortValues: [1, 2, 3, 4, 5],
      type: "update_row",
    };

    act(() => {
      server.send(updateMessage);
    });

    // ensure column headers are set
    table.columns.forEach((col, index) => {
      const element = screen.getByTestId(`col-${index}`);
      expect(element).toBeInTheDocument();
      expect(element.textContent).toBe(col.name);
    });

    // ensure old rows are still present
    table.rows.forEach((row, index) => {
      const element = screen.getByTestId(`row-${row.id}`);
      expect(element).toBeInTheDocument();
      row.values.forEach((value, index) => {
        const cell = screen.getByTestId(`cell-${row.id}-${index}`);
        expect(cell).toBeInTheDocument();
        expect(cell.textContent).toBe(value);
      });
    });

    // ensure new row is present as well
    const rowElement = screen.getByTestId(`row-${newRowId}`);
    expect(rowElement).toBeInTheDocument();
    updateMessage.values.forEach((value, index) => {
      const cell = screen.getByTestId(`cell-${newRowId}-${index}`);
      expect(cell).toBeInTheDocument();
      expect(cell.textContent).toBe(value);
    });
  });
});
