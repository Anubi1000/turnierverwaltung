import { describe, expect, it } from "@jest/globals";
import { Scoreboard } from "@/app/scoreboard";
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom/jest-globals";

describe("No tournament test", () => {
  it("checks if no tournament selected text is displayed", () => {
    render(<Scoreboard />);

    const errorText = screen.getByRole("heading", { level: 4 });

    expect(errorText).toBeInTheDocument();
    expect(errorText.textContent).toBe("Kein Turnier ausgewählt");
  });
});
