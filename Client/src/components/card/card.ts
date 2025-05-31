import { tv } from "tailwind-variants";

export const cardStyles = tv({
  slots: {
    root: "flex flex-col rounded-xl bg-surface-0 text-surface-700 shadow-[0_1px_3px_0_rgba(0,0,0,0.1),0_1px_2px_-1px_rgba(0,0,0,0.1)] transition-colors duration-200 p-5 select-none",
    title: "font-medium text-xl",
  },
  variants: {
    selectable: {
      true: {
        root: "hover:bg-primary-100",
      },
    },
    selected: {
      true: {
        root: "bg-primary-100/50",
      },
    },
  },
});

export interface CardProps {
  title: string;
  content?: string;

  selectable?: boolean;
  selected?: boolean;
  class?: string;
}
