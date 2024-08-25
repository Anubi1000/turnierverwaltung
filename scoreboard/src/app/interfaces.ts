export interface Column {
  name: string;
  width: string;
}

export interface Row {
  id: string;
  values: string[];
}

export interface DisciplineDataTable {
  id: string;
  name: string;
  columns: Column[];
  rows: Row[];
}

export interface SetTournamentMessage {
  title: string;
  tables: DisciplineDataTable[];
  type: "set_tournament";
}
