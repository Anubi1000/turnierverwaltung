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
      label: strings.delete,
      severity: "danger",
    },
    rejectProps: {
      label: strings.cancel,
      text: true,
    },
    accept: accept,
  };
}
