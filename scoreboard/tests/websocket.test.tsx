import { afterEach, beforeEach, describe, expect, it } from "@jest/globals";
import Page from "@/app/page";
import { render, screen } from "@testing-library/react";
import { act } from "react";
import "@testing-library/jest-dom/jest-globals";
import "@testing-library/jest-dom";
import WS from "jest-websocket-mock";
import { ScoreboardData, SetTournamentMessage } from "@/interfaces";

function createDummyData() {
  const data: ScoreboardData = {
    name: "Scoreboard",
    tables: [
      {
        name: "Discipline",
        columns: [],
        rows: [],
      },
    ],
  };

  for (let i = 0; i < 5; i++) {
    data.tables[0].columns.push({
      name: `Col ${i}`,
      alignment: "center",
      width: { weight: 20 },
    });
  }
  for (let i = 0; i < 10; i++) {
    data.tables[0].rows.push({
      values: ["2", "4", "6", "8", "10"],
    });
  }
  return data;
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

    const data = createDummyData();
    const message: SetTournamentMessage = {
      data: data,
      type: "set_tournament",
    };
    const table = data.tables[0];

    act(() => {
      server.send(message);
    });

    const title = screen.getByText(message.data.name);
    expect(title).toBeInTheDocument();

    // ensure column headers are set
    table.columns.forEach((col, index) => {
      const element = screen.getByTestId(`col-${index}`);
      expect(element).toBeInTheDocument();
      expect(element.textContent).toBe(col.name);
    });

    table.rows.forEach((row, rowIndex) => {
      const element = screen.getByTestId(`row-${rowIndex}`);
      expect(element).toBeInTheDocument();
      row.values.forEach((value, index) => {
        const cell = screen.getByTestId(`cell-${rowIndex}-${index}`);
        expect(cell).toBeInTheDocument();
        expect(cell.textContent).toBe(value);
      });
    });
  });
});
