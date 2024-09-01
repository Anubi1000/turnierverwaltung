import { describe, expect, it } from "@jest/globals";
import { render, screen } from "@testing-library/react";
import '@testing-library/jest-dom/jest-globals'
import { DisciplineTable } from "@/app/disciplineTable";
import { TournamentTable, TournamentTableColumn } from "@/app/interfaces";

describe("No columns test", () => {
  let moveNext= function () {}
  let table: TournamentTable = {
    id: "",
    name: "",
    columns: [],
    rows: []
  }
  it("checks if no column definition text is displayed", () => {
    render(<DisciplineTable 
      moveNext={
        moveNext
      } 
      table={ 
        table
      }
    />);

    const errorText = screen.getByRole("heading", { level: 4 });

    expect(errorText).toBeInTheDocument();
    expect(errorText.textContent).toBe("Keine Spaltendefinition vorhanden");
  });
});
