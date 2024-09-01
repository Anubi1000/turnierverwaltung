import { describe, expect, it } from "@jest/globals";
import { render, screen } from "@testing-library/react";
import { DisciplineTable } from "@/app/disciplineTable";
import { TournamentTable } from "@/app/interfaces";

describe("DisciplineTable", () => {
  it("check if no column definition text is displayed", () => {
    const table: TournamentTable = {
      columns: [],
      id: "",
      name: "",
      rows: [],
    };

    render(<DisciplineTable moveNext={() => {}} table={table} />);

    const errorText = screen.getByRole("heading", { level: 4 });

    expect(errorText).toBeInTheDocument();
    expect(errorText.textContent).toBe("Keine Spaltendefinition vorhanden");
  });

  it("check if no rows text is displayed", () => {
    const table: TournamentTable = {
      columns: [
        {
          name: "",
          width: "",
          alignment: "center",
        },
      ],
      id: "",
      name: "",
      rows: [],
    };

    render(<DisciplineTable moveNext={() => {}} table={table} />);

    const errorText = screen.getByRole("heading", { level: 4 });

    expect(errorText).toBeInTheDocument();
    expect(errorText.textContent).toBe("Keine Einträge vorhanden");
  });
});
