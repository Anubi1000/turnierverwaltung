import type {
  ScoreboardDataTableColumn,
  ScoreboardDataTableColumnAlignment,
} from "./api/schemas";

function getColumnAlignment(alignment: ScoreboardDataTableColumnAlignment) {
  switch (alignment) {
    case "Left":
      return "left";
    case "Center":
      return "center";
    case "Right":
      return "right";
  }
}

function getCss(selector: string, styles: Partial<CSSStyleDeclaration>) {
  let css = selector + "{";
  for (const [key, value] of Object.entries(styles)) {
    const kebabKey = key.replace(
      /[A-Z]/g,
      (match) => "-" + match.toLowerCase(),
    );
    css += `${kebabKey}:${value};`;
  }
  css += "}";
  return css;
}

const WidthConversionFactor = 0.6;

export function getTableStyles(
  columns: ScoreboardDataTableColumn[],
  tableId: string,
) {
  let css = "";

  css += getCss(`#${tableId} tr`, {
    display: "flex",
    width: "100%",
    alignItems: "center",
    maxHeight: "4rem",
  });

  css += getCss(`#${tableId} tr:nth-child(even)`, {
    backgroundColor: "var(--p-primary-100)",
    borderRadius: "8px",
  });

  for (let i = 0; i < columns.length; i++) {
    const column = columns[i];
    const nth = i + 1;
    const styles: Partial<CSSStyleDeclaration> = {
      textAlign: getColumnAlignment(column.alignment),
      paddingBlock: "12px",
      paddingInline: "6px",
    };

    switch (column.width.type) {
      case "fixed":
        styles["minWidth"] = `${column.width.width * WidthConversionFactor}px`;
        break;
      case "variable":
        styles["flex"] = `${column.width.weight} 0 0`;
        styles["overflow"] = "hidden";
        styles["textOverflow"] = "ellipsis";
        break;
    }

    css += getCss(
      `#${tableId} th:nth-child(${nth}), #${tableId} td:nth-child(${nth})`,
      styles,
    );
  }

  return css;
}
