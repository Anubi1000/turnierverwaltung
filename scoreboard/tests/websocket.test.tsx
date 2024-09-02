import { beforeEach, describe, expect, it, test } from "@jest/globals";
import Page from "@/app/page";
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom/jest-globals";
import "@testing-library/jest-dom";
import WS from "jest-websocket-mock";
import {
  SetTournamentMessage,
  TournamentTable,
  UpdateRowMessage,
} from "@/app/interfaces";

describe("web socket tests", () => {
  it("check if tournament is set", async () => {
    render(<Page />);

    const server = new WS("ws://127.0.0.1:8080/ws", { jsonProtocol: true });
    await server.connected;

    const table: TournamentTable = {
      id: "Table",
      name: "Discipline",
      columns: [],
      rows: [],
    };
    const message: SetTournamentMessage = {
      tables: [table],
      title: "Test",
      type: "set_tournament",
    };
    server.send(message);

    const titles = screen.getAllByRole("heading", { level: 6 });

    const title = titles[0];
    expect(title).toBeInTheDocument();
    expect(title.textContent).toBe("Test");

    const clock = titles[1];
    expect(clock).toBeInTheDocument();

    // how to get attribute of scoreboard to check if tables match?
    WS.clean();
  });

  it("check if updates are done correctly", async () => {
    // how to pass inital tournament to scoreboard?
    render(<Page />);

    const server = new WS("ws://127.0.0.1:8080/ws", { jsonProtocol: true });
    await server.connected;

    // apply update
    const message: UpdateRowMessage = {
      tableId: "",
      rowId: "",
      values: [],
      sortValues: [],
      type: "update_row",
    };
    server.send(message);

    // again need to check attributes

    WS.clean();
  });
});
