import type { ConfirmationOptions } from "primevue/confirmationoptions";

export class Dialogs {
  static confirmDelete(
    itemName: string | undefined,
    accept: () => void,
  ): ConfirmationOptions {
    if (!itemName) itemName = "NO NAME";
    return {
      message: `Möchtest du "${itemName}" wirklich löschen?`,
      header: "Löschen bestätigen",
      rejectProps: {
        label: "Abbrechen",
        severity: "secondary",
        outlined: true,
      },
      acceptProps: { label: "Löschen", severity: "danger" },
      accept: accept,
    };
  }
}
