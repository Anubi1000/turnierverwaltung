import type { ScoreboardDataTableColumn } from "@/utils/api/schemas";
import { getColumnAlignment, getCss } from "@/utils/scoreboardTable.ts";
import type { DeepReadonly } from "vue";

const WidthConversionFactor = 0.04;

export function getTableStyles(
  columns: DeepReadonly<ScoreboardDataTableColumn[]>,
  tableId: string,
) {
  let css = "";

  css += getCss(`#${tableId} tr`, {
    display: "flex",
    width: "100%",
    alignItems: "center",
    maxHeight: "4rem",
  });

  function addRowColor(selector: string, value: string) {
    css += getCss(`#${tableId} > tbody > tr:nth-child(${selector})`, {
      backgroundColor: value,
    });
  }

  addRowColor("even", "var(--p-primary-100)");

  addRowColor("1", "#ffd700");
  addRowColor("2", "#c0c0c0");
  addRowColor("3", "#bf8970");

  for (let i = 0; i < columns.length; i++) {
    const column = columns[i];
    const nth = i + 1;
    const styles: Partial<CSSStyleDeclaration> = {
      textAlign: getColumnAlignment(column.alignment),
      paddingBlock: "0.75rem",
      paddingInline: "0.5rem",
    };

    switch (column.width.type) {
      case "fixed":
        styles["minWidth"] = `${column.width.width * WidthConversionFactor}rem`;
        break;
      case "variable":
        styles["flex"] = `${column.width.weight} 0 0`;
        styles["overflow"] = "hidden";
        styles["textOverflow"] = "ellipsis";
        break;
    }

    css += getCss(`#${tableId} td:nth-child(${nth})`, styles);
  }

  return css;
}
