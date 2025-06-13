import type { FunctionalComponent } from "vue";
import type { RouteLocationRaw } from "vue-router";

export interface ItemListItem {
  id: string;
  title: string;
  link?: RouteLocationRaw;
  content?: string;
}

export interface NavigationBarItem {
  label: string;
  icon: FunctionalComponent;
  to: RouteLocationRaw;
}
