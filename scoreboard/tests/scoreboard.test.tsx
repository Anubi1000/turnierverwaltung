import { describe, expect, it } from "@jest/globals";
import { Scoreboard } from "@/app/scoreboard";
import { render, screen } from "@testing-library/react";

describe("Scoreboard", () => {
  it("shows not tournament selected text", () => {
    render(<Scoreboard />);

    const errorText = screen.getByRole("heading", { level: 4 });

    expect(errorText).toBeInTheDocument();
    expect(errorText.textContent).toBe("Kein Turnier ausgewählt");
  });
});
