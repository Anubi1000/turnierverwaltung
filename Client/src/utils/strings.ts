import { Gender, type TeamScoreDisplayType } from "@/utils/api/schemas";

export const strings = {
  appName: "Turnierverwaltung",

  general: "Allgemein",
  other: "Sonstiges",
  results: "Ergebnisse",
  overview: "Übersicht",

  scoreboard: "Scoreboard",
  searchTerm: "Suchbegriff",
  newRound: "Neue Runde",
  round: "Runde",

  status: {
    loading: "Lädt...",
    listLoadingFailed: "Das Laden der Liste ist fehlgeschlagen",
    noEntriesAvailable: "Keine Einträge vorhanden",
    noResultsFound: "Keine Ergebnisse gefunden",
    overviewLoadingFailed: "Das Laden der Übersicht ist fehlgeschlagen",
    atLeastOneSelectedTable:
      "Es muss mindestens eine Tabelle ausgewählt werden",
  },

  actions: {
    login: "Anmelden",
    save: "Speichern",
    delete: "Löschen",
    cancel: "Abbrechen",
    edit: "Bearbeiten",
    back: "Zurück",
  },

  name: "Name",
  date: "Datum",
  teamSize: "Teamgröße",
  teamSizeFixed: "Teamgröße fix",
  memberCount: "Anzahl Mitglieder",
  startNumber: "Startnummer",
  gender: "Geschlecht",
  gendersSeparated: "Geschlechter getrennt",
  amountOfBestRoundsToShow: "Anzahl Runden auf Anzeige",
  isAdded: "Wird addiert",
  basedOn: "Basiert auf",
  type: "Typ",
  members: "Mitglieder",
  isShownInResult: "Wird in Ergebnis angezeigt",

  scores: {
    showOnScoreboard: "Auf Scoreboard anzeigen",
    downloadOverview: "Übersicht herunterladen",
    exportTables: "Tabellen zu exportieren",
    separateDocuments: "Separate Dokumente",
  },

  tournament: {
    item: "Turnier",
    items: "Turniere",
    create: "Neues Turnier",
    edit: "Turnier bearbeiten",
    selectOne: "Wähle ein Turnier aus",
    loadingError: "Beim Laden des Turniers ist ein Fehler aufgetreten",
    open: "Tunier öffnen",
    deleteDlg: {
      header: "Tunier löschen?",
      message: (name: string) =>
        `Möchtest du das Turnier "${name}" wirklich löschen?`,
    },
  },

  club: {
    item: "Verein",
    items: "Vereine",
    create: "Neuer Verein",
    edit: "Verein bearbeiten",
    selectOne: "Wähle einen Verein aus",
    loadingError: "Beim Laden des Vereins ist ein Fehler aufgetreten",
    count: "Anzahl Vereine",
    deleteDlg: {
      header: "Verein löschen?",
      message: (name: string) =>
        `Möchtest du den Verein "${name}" wirklich löschen?`,
    },
  },

  participant: {
    item: "Teilnehmer",
    items: "Teilnehmer",
    create: "Neuer Teilnehmer",
    edit: "Teilnehmer bearbeiten",
    selectOne: "Wähle einen Teilnehmer aus",
    loadingError: "Beim Laden des Teilnehmers ist ein Fehler aufgetreten",
    count: "Anzahl Teilnehmer",
    deleteDlg: {
      header: "Teilnehmer löschen?",
      message: (name: string) =>
        `Möchtest du den Teilnehmer "${name}" wirklich löschen?`,
    },

    editResults: "Ergebnisse bearbeiten",
  },

  team: {
    item: "Team",
    items: "Teams",
    create: "Neues Team",
    edit: "Team bearbeiten",
    selectOne: "Wähle ein Team aus",
    loadingError: "Beim Laden des Teams ist ein Fehler aufgetreten",
    count: "Anzahl Teams",
    notParticipatingInDisciplines:
      "Dieses Team nimmt an keiner Team-Disziplin teil",
    deleteDlg: {
      header: "Team löschen?",
      message: (name: string) =>
        `Möchtest du das Team "${name}" wirklich löschen?`,
    },
  },

  discipline: {
    item: "Disziplin",
    items: "Disziplinen",
    create: "Neue Disziplin",
    edit: "Disziplin bearbeiten",
    selectOne: "Wähle eine Disziplin aus",
    loadingError: "Beim Laden der Disziplin ist ein Fehler aufgetreten",
    count: "Anzahl Disziplinen",
    noAvailable: "Es sind Disziplinen vorhanden",
    deleteDlg: {
      header: "Disziplin löschen?",
      message: (name: string) =>
        `Möchtest du die Disziplin "${name}" wirklich löschen?`,
    },
  },

  teamDiscipline: {
    item: "Team-Disziplin",
    items: "Team-Disziplinen",
    create: "Neue Team-Disziplin",
    edit: "Team-Disziplin bearbeiten",
    selectOne: "Wähle eine Team-Disziplin aus",
    loadingError: "Beim Laden der Team-Disziplin ist ein Fehler aufgetreten",
    deleteDlg: {
      header: "Team-Disziplin löschen?",
      message: (name: string) =>
        `Möchtest du die Team-Disziplin "${name}" wirklich löschen?`,
    },
  },

  value: {
    items: "Werte",
    create: "Neuer Wert",
  },

  notFound: {
    pageNotFound: "Seite nicht gefunden",
    backToStart: "Zurück zum Start",
  },

  validation: {
    nameNeeded: "Es muss ein Name angegeben werden",
    numberNeeded: "Es muss eine Zahl angegeben werden",
    dateNeeded: "Es muss ein Datum angegeben werden",
    clubNeeded: "Es muss ein Verein angegeben werden",
    valueNeeded: "Es muss mindestens einen Wert geben",
    disciplineNeeded: "Es muss mindestens eine Disziplin angegeben werden",
    memberNeeded: "Es muss mindestens ein Mitglied geben",
    exactlyMembersNeeded: (teamSize: number) =>
      `Das Team muss genau ${teamSize} Mitglieder haben`,
  },

  formatting: {
    date: (date: number) => {
      const dateObj = new Date(date);

      return dateObj.toLocaleDateString("de-DE", {
        day: "2-digit",
        month: "long",
        year: "numeric",
      });
    },
    boolean: (bool: boolean) => {
      return bool ? "Ja" : "Nein";
    },
    gender: (gender: Gender) => {
      switch (gender) {
        case "Male":
          return "Männlich";
        case "Female":
          return "Weiblich";
      }
    },
    teamScoreDisplayType: (displayType: TeamScoreDisplayType) => {
      switch (displayType) {
        case "Normal":
          return "Normal";
        case "Nationcup":
          return "Nationencup";
        case "Triathlon":
          return "Triathlon";
      }
    },
  },
} as const;
