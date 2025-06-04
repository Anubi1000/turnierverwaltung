import { strings } from "@/utils/strings.ts";
import type { ConfirmationOptions } from "primevue/confirmationoptions";

export function createDeleteDialog(
  header: string,
  message: string,
  accept: ConfirmationOptions["accept"],
): ConfirmationOptions {
  return {
    header: header,
    message: message,
    modal: true,
    acceptProps: {
      label: strings.actions.delete,
      severity: "danger",
    },
    rejectProps: {
      label: strings.actions.cancel,
      text: true,
    },
    accept: accept,
  };
}
