import { tv } from "tailwind-variants";

export const navigationItemStyles = tv({
  base: "flex items-center py-2 px-4 gap-2 text-lg font-medium rounded-lg mx-2 select-none transition-colors hover:bg-primary-100",
  variants: {
    selected: {
      true: "bg-primary-100/50",
    },
  },
});

export interface NavigationItemProps {
  selected?: boolean;
}
