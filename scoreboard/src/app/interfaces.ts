export interface SetTournamentMessage {
  title: string;
  tables: TournamentTable[];
  type: "set_tournament";
}

export interface TournamentTable {
  id: string;
  name: string;
  columns: TournamentTableColumn[];
  rows: TournamentTableRow[];
}

export interface TournamentTableColumn {
  name: string;
  width: string;
  alignment: "left" | "center" | "right";
}

export interface TournamentTableRow {
  id: string;
  values: string[];
  sortValues: string[];
}

export interface Tournament {
  title: string;
  tables: TournamentTable[];
}

export interface UpdateRowMessage {
  tableId: string;
  rowId: string;
  values: string[];
  sortValues: string[];
  type: "update_row";
}
