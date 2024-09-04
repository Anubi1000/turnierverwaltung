import { describe, expect, it } from "@jest/globals";
import Page from "@/app/page";
import { render, screen } from "@testing-library/react";
import {act} from 'react';
import "@testing-library/jest-dom/jest-globals";
import "@testing-library/jest-dom";
import WS from "jest-websocket-mock";
import {
  SetTournamentMessage,
  TournamentTable,
  UpdateRowMessage,
  Tournament
} from "@/app/interfaces";
import WebSocketComponent from "@/websocketComponent";

describe("web socket tests", () => {
  it("check if tournament is set", async () => {
    act(() => {
      render(<WebSocketComponent tournamentProp={null}/>);
    });
    
    const server = new WS("ws://127.0.0.1:8080/ws", { jsonProtocol: true });
    await server.connected;

    const table: TournamentTable = {
      id: "Table",
      name: "Discipline",
      columns: [{
        name: "Col1",
        alignment: "center",
        width: "50%"
      }, {
        name: "Col2",
        alignment: "center",
        width: "50%"
      }],
      rows: [{
        values: ["1", "2"],
        id: "OnlyRow",
        sortValues: [1,2]
      }],
    };
    const message: SetTournamentMessage = {
      tables: [table],
      title: "Test",
      type: "set_tournament",
    };

    act(() => {
      server.send(message);
    });

    const titles = screen.getAllByRole("heading", { level: 6 });

    // ensure title is correct
    const title = titles[0];
    expect(title).toBeInTheDocument();
    expect(title.textContent).toBe("Test");

    // ensure clock is displayed
    const clock = titles[1];
    expect(clock).toBeInTheDocument();


    // ensure both column headers are set
    const col1 = screen.getByTestId("Col1")
    const col2 = screen.getByTestId("Col2")
    expect(col1).toBeInTheDocument()
    expect(col2).toBeInTheDocument()

    // ensure single row is set and values amtch
    const rows = screen.getAllByTestId("OnlyRow")    
    rows.forEach(row => {
      expect(row).toBeInTheDocument()
      expect(row.textContent).toBe("12")
    });

    WS.clean();
  });

  it("check if updates are done correctly", async () => {
    // how to pass inital tournament to scoreboard?
    const table: TournamentTable = {
      id: "Table",
      name: "Discipline",
      columns: [{
        name: "Col1",
        alignment: "center",
        width: "50%"
      }, {
        name: "Col2",
        alignment: "center",
        width: "50%"
      }],
      rows: [{
        values: ["1", "2"],
        id: "OnlyRow",
        sortValues: [1,2]
      }],
    };

    const tournament: Tournament = {
      title: "Title",
      tables: [table]
    }

    // render with table above
    act(() => {
      render(<WebSocketComponent tournamentProp={tournament} />);
    });


    var rows = screen.getAllByTestId("OnlyRow")    
    rows.forEach(row => {
      expect(row).toBeInTheDocument()
      expect(row.textContent).toBe("12") // initial values from table
    });
    
    const server = new WS("ws://127.0.0.1:8080/ws", { jsonProtocol: true });
    await server.connected;

    // apply update
    const message: UpdateRowMessage = {
      tableId: "Table",
      rowId: "OnlyRow",
      values: ["3","4"],
      sortValues: [1,2],
      type: "update_row",
    };

    act(() => {
      server.send(message);
    });

    const titles = screen.getAllByRole("heading", { level: 6 });
    // ensure title is correct
    const title = titles[0];
    expect(title).toBeInTheDocument();
    expect(title.textContent).toBe("Title");

    // ensure clock is displayed
    const clock = titles[1];
    expect(clock).toBeInTheDocument();

    // ensure both column headers are set
    const col1 = screen.getByTestId("Col1")
    const col2 = screen.getByTestId("Col2")
    expect(col1).toBeInTheDocument()
    expect(col2).toBeInTheDocument()

    // ensure single row is set and values in row match updated values
    rows = screen.getAllByTestId("OnlyRow")    
    rows.forEach(row => {
      expect(row).toBeInTheDocument()
      expect(row.textContent).toBe("34")
    });

    WS.clean();
  });
});
