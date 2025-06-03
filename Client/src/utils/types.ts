import type { RouteLocationRaw } from "vue-router";

export interface ItemListItem {
  id: string;
  title: string;
  link: RouteLocationRaw;
  content?: string;
}

export interface ItemListItemRaw {
  id: string;
  title: string;
  content?: string;
}
