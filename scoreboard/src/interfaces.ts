export interface Tournament {
  title: string;
  data: ScoreboardData;
}

export interface ScoreboardData {
  name: string;
  tables: ScoreboardData_Table[];
}

export interface ScoreboardData_Table {
  name: string;
  columns: ScoreboardData_Table_Column[];
  rows: ScoreboardData_Table_Row[];
}

export interface ScoreboardData_Table_Column {
  name: string;
  width: { width: number } | { weight: number };
  alignment: "left" | "center" | "right";
}

export interface ScoreboardData_Table_Row {
  values: string[];
}

export interface SetTournamentMessage {
  data: ScoreboardData;
  type: "set_tournament";
}

export function checkIfColIsFixed(
  obj: { width: number } | { weight: number },
): obj is { width: number } {
  return (obj as { width: number }).width !== undefined;
}
