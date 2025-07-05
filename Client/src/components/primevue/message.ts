import { tv } from "tailwind-variants";

export const messageStyles = tv({
  slots: {
    root: "",
    content:
      "flex items-center px-3 py-2 gap-2 h-full text-base font-medium select-none",
  },
  variants: {
    severity: {
      info: {
        root: "bg-blue-50/95 outline-blue-200 text-blue-600 shadow-[0px_4px_8px_0px_theme(colors.blue.500/0.04)]",
      },
      secondary: {
        root: "bg-surface-100 outline-surface-200 text-surface-600 shadow-[0px_4px_8px_0px_rgba(0,0,0,0.04)]",
      },
      error: {
        root: "bg-red-50/95 outline-red-200 text-red-600 shadow-[0px_4px_8px_0px_theme(colors.red.500/0.04)]",
      },
    },
    variant: {
      default: {
        root: "rounded-md outline outline-1",
      },
      simple: {
        root: "bg-transparent outline-none shadow-none",
        content: "p-0",
      },
    },
  },
  compoundVariants: [
    {
      severity: "info",
      variant: "simple",
      class: {
        root: "text-blue-500",
      },
    },
    {
      severity: "secondary",
      variant: "simple",
      class: {
        root: "text-surface-500",
      },
    },
    {
      severity: "error",
      variant: "simple",
      class: {
        root: "text-red-500",
      },
    },
  ],
  defaultVariants: {
    severity: "info",
    variant: "default",
  },
});

export interface MessageProps {
  severity?: "info" | "secondary" | "error";
  variant?: "default" | "simple";

  class?: string;
}
