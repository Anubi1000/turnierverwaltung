import { describe, expect, it } from "@jest/globals";
import { render, screen } from "@testing-library/react";
import { DisciplineTable } from "@/app/disciplineTable";
import { ScoreboardData_Table } from "@/interfaces";

describe("DisciplineTable", () => {
  it("check if no column definition text is displayed", () => {
    const table: ScoreboardData_Table = {
      columns: [],
      name: "",
      rows: [],
    };

    render(<DisciplineTable moveNext={() => {}} table={table} />);

    const errorText = screen.getByRole("heading", { level: 4 });

    expect(errorText).toBeInTheDocument();
    expect(errorText.textContent).toBe("Keine Spaltendefinition vorhanden");
  });

  it("check if no rows text is displayed", () => {
    const table: ScoreboardData_Table = {
      columns: [
        {
          name: "",
          width: { width: 250 },
          alignment: "center",
        },
      ],
      name: "",
      rows: [],
    };

    render(<DisciplineTable moveNext={() => {}} table={table} />);

    const errorText = screen.getByRole("heading", { level: 4 });

    expect(errorText).toBeInTheDocument();
    expect(errorText.textContent).toBe("Keine Einträge vorhanden");
  });
});
