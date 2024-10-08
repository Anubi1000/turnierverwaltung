import { describe, expect, it } from "@jest/globals";
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom/jest-globals";
import { DisciplineCarousel } from "@/app/disciplineCarousel";

describe("DisciplineCarousel", () => {
  it("check if no disciplines available text is displayed", () => {
    render(<DisciplineCarousel tables={[]} />);

    const errorText = screen.getByRole("heading", { level: 4 });

    expect(errorText).toBeInTheDocument();
    expect(errorText.textContent).toBe("Keine Disziplinen vorhanden");
  });
});
