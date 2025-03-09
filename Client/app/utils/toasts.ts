import type { ToastMessageOptions } from "primevue";
import type { FetchError } from "ofetch";

export class Toasts {
  private static standardToastTime = 5000;

  static itemCreateSuccess(
    itemName: string,
    itemArticle: string,
  ): ToastMessageOptions {
    return {
      severity: "success",
      summary: `${itemName} erstellt`,
      detail: `${itemArticle} ${itemName} wurde erfolgreich erstellt`,
      life: this.standardToastTime,
    };
  }

  static itemCreateFail(
    error: FetchError,
    itemNameWithArticle: string,
  ): ToastMessageOptions {
    return {
      severity: "error",
      summary: `Erstellen fehlgeschlagen (${error.status})`,
      detail: `${itemNameWithArticle} konnte nicht erstellt werden`,
      life: this.standardToastTime,
    };
  }

  static itemDeleteSuccess(
    itemName: string,
    itemArticle: string,
  ): ToastMessageOptions {
    return {
      severity: "success",
      summary: `${itemName} gelöscht`,
      detail: `${itemArticle} ${itemName} wurde erfolgreich gelöscht`,
      life: this.standardToastTime,
    };
  }

  static itemDeleteFail(
    error: FetchError,
    itemNameWithArticle: string,
  ): ToastMessageOptions {
    return {
      severity: "error",
      summary: `Löschen fehlgeschlagen (${error.status})`,
      detail: `${itemNameWithArticle} konnte nicht gelöscht werden`,
      life: this.standardToastTime,
    };
  }

  static itemUpdateSuccess(
    itemName: string,
    itemArticle: string,
  ): ToastMessageOptions {
    return {
      severity: "success",
      summary: `${itemName} bearbeitet`,
      detail: `${itemArticle} ${itemName} wurde erfolgreich bearbeitet`,
      life: this.standardToastTime,
    };
  }

  static itemUpdateFail(
    error: FetchError,
    itemNameWithArticle: string,
  ): ToastMessageOptions {
    return {
      severity: "error",
      summary: `Bearbeiten fehlgeschlagen (${error.status})`,
      detail: `${itemNameWithArticle} konnte nicht bearbeitet werden`,
      life: this.standardToastTime,
    };
  }

  static itemModifySuccess(
    itemName: string,
    itemArticle: string,
    operation: "create" | "edit",
  ) {
    switch (operation) {
      case "create":
        return this.itemCreateSuccess(itemName, itemArticle);
      case "edit":
        return this.itemUpdateSuccess(itemName, itemArticle);
    }
  }

  static itemModifyFail(
    error: FetchError,
    itemNameWithArticle: string,
    operation: "create" | "edit",
  ) {
    switch (operation) {
      case "create":
        return this.itemCreateFail(error, itemNameWithArticle);
      case "edit":
        return this.itemUpdateFail(error, itemNameWithArticle);
    }
  }
}
