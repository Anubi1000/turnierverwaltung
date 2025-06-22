import { useAuthStore } from "@/utils/stores.ts";
import type { RouteRecordRaw } from "vue-router";

export const RouteNames = {
  ROOT: "root",

  SCOREBOARD: "scoreboard",

  TOURNAMENT_LIST: "tournamentList",
  TOURNAMENT_DETAIL: "tournamentDetail",
  TOURNAMENT_CREATE: "tournamentCreate",
  TOURNAMENT_EDIT: "tournamentEdit",

  TOURNAMENT_SCORES: "tournamentOverview",

  CLUB_LIST: "clubList",
  CLUB_DETAIL: "clubDetail",
  CLUB_CREATE: "clubCreate",
  CLUB_EDIT: "clubEdit",

  PARTICIPANT_LIST: "participantList",
  PARTICIPANT_DETAIL: "participantDetail",
  PARTICIPANT_CREATE: "participantCreate",
  PARTICIPANT_EDIT: "participantEdit",
  PARTICIPANT_RESULTS: "participantResults",

  TEAM_LIST: "teamList",
  TEAM_DETAIL: "teamDetail",
  TEAM_CREATE: "teamCreate",
  TEAM_EDIT: "teamEdit",

  DISCIPLINE_LIST: "disciplineList",
  DISCIPLINE_DETAIL: "disciplineDetail",
  DISCIPLINE_CREATE: "disciplineCreate",
  DISCIPLINE_EDIT: "disciplineEdit",

  TEAM_DISCIPLINE_LIST: "teamDisciplineList",
  TEAM_DISCIPLINE_DETAIL: "teamDisciplineDetail",
  TEAM_DISCIPLINE_CREATE: "teamDisciplineCreate",
  TEAM_DISCIPLINE_EDIT: "teamDisciplineEdit",
} as const;

export const LayoutNames = {
  TOURNAMENT_LIST: "layout_tournamentList",
  CLUB_LIST: "layout_clubList",
  PARTICIPANT_LIST: "layout_participantList",
  TEAM_LIST: "layout_teamList",
  DISCIPLINE_LIST: "layout_disciplineList",
  TEAM_DISCIPLINE_LIST: "layout_teamDisciplineList",
} as const;

export const routes: RouteRecordRaw[] = [
  {
    path: "/:path(.*)*",
    component: () => import("@/pages/NotFound.vue"),
  },
  {
    path: "/sidebar",
    component: () => import("@/pages/sidebar/SidebarPage.vue"),
  },
  {
    name: RouteNames.ROOT,
    path: "/",
    component: () => import("@/pages/Index.vue"),
    async beforeEnter() {
      const authStore = useAuthStore();
      if (!authStore.checkedAuth) await authStore.checkAuthentication();
    },
  },
  {
    name: RouteNames.SCOREBOARD,
    path: "/scoreboard",
    component: () => import("@/pages/scoreboard/Scoreboard.vue"),
  },
  {
    path: "/dashboard",
    meta: { requiresAuth: true },
    component: () => import("@/pages/DashboardLayout.vue"),
    children: [
      {
        name: LayoutNames.TOURNAMENT_LIST,
        path: "tournaments",
        meta: { requiresAuth: true },
        component: () => import("@/pages/tournaments/TournamentListLayout.vue"),
        children: [
          {
            name: RouteNames.TOURNAMENT_LIST,
            path: "",
            meta: { requiresAuth: true },
            component: () =>
              import("@/pages/tournaments/TournamentNoSelection.vue"),
          },
          {
            name: RouteNames.TOURNAMENT_DETAIL,
            path: ":tournamentId(\\d+)",
            meta: { requiresAuth: true },
            component: () => import("@/pages/tournaments/TournamentDetail.vue"),
          },
          {
            name: RouteNames.TOURNAMENT_CREATE,
            path: "create",
            meta: { requiresAuth: true },
            component: () => import("@/pages/tournaments/TournamentCreate.vue"),
          },
          {
            name: RouteNames.TOURNAMENT_EDIT,
            path: ":tournamentId(\\d+)/edit",
            meta: { requiresAuth: true },
            component: () => import("@/pages/tournaments/TournamentEdit.vue"),
          },
        ],
      },
    ],
  },
  {
    path: "/dashboard/tournaments/:tournamentId(\\d+)/overview",
    meta: { requiresAuth: true },
    component: () => import("@/pages/tournaments/TournamentOverviewLayout.vue"),
    children: [
      {
        name: RouteNames.TOURNAMENT_SCORES,
        path: "scores",
        meta: { requiresAuth: true },
        component: () => import("@/pages/scores/Scores.vue"),
      },
      {
        name: LayoutNames.CLUB_LIST,
        path: "clubs",
        meta: { requiresAuth: true },
        component: () => import("@/pages/clubs/ClubListLayout.vue"),
        children: [
          {
            name: RouteNames.CLUB_LIST,
            path: "",
            meta: { requiresAuth: true },
            component: () => import("@/pages/clubs/ClubNoSelection.vue"),
          },
          {
            name: RouteNames.CLUB_DETAIL,
            path: ":clubId(\\d+)",
            meta: { requiresAuth: true },
            component: () => import("@/pages/clubs/ClubDetail.vue"),
          },
          {
            name: RouteNames.CLUB_CREATE,
            path: "create",
            meta: { requiresAuth: true },
            component: () => import("@/pages/clubs/ClubCreate.vue"),
          },
          {
            name: RouteNames.CLUB_EDIT,
            path: ":clubId(\\d+)/edit",
            meta: { requiresAuth: true },
            component: () => import("@/pages/clubs/ClubEdit.vue"),
          },
        ],
      },
      {
        name: LayoutNames.PARTICIPANT_LIST,
        path: "participants",
        meta: { requiresAuth: true },
        component: () =>
          import("@/pages/participants/ParticipantListLayout.vue"),
        children: [
          {
            name: RouteNames.PARTICIPANT_LIST,
            path: "",
            meta: { requiresAuth: true },
            component: () =>
              import("@/pages/participants/ParticipantNoSelection.vue"),
          },
          {
            name: RouteNames.PARTICIPANT_DETAIL,
            path: ":participantId(\\d+)",
            meta: { requiresAuth: true },
            component: () =>
              import("@/pages/participants/ParticipantDetail.vue"),
          },
          {
            name: RouteNames.PARTICIPANT_CREATE,
            path: "create",
            meta: { requiresAuth: true },
            component: () =>
              import("@/pages/participants/ParticipantCreate.vue"),
          },
          {
            name: RouteNames.PARTICIPANT_EDIT,
            path: ":participantId(\\d+)/edit",
            meta: { requiresAuth: true },
            component: () => import("@/pages/participants/ParticipantEdit.vue"),
          },
          {
            name: RouteNames.PARTICIPANT_RESULTS,
            path: ":participantId(\\d+)/results/:disciplineId(\\d+)",
            meta: { requiresAuth: true },
            component: () =>
              import("@/pages/participants/result/ParticipantResults.vue"),
          },
        ],
      },
      {
        name: LayoutNames.DISCIPLINE_LIST,
        path: "disciplines",
        meta: { requiresAuth: true },
        component: () => import("@/pages/disciplines/DisciplineListLayout.vue"),
        children: [
          {
            name: RouteNames.DISCIPLINE_LIST,
            path: "",
            meta: { requiresAuth: true },
            component: () =>
              import("@/pages/disciplines/DisciplineNoSelection.vue"),
          },
          {
            name: RouteNames.DISCIPLINE_DETAIL,
            path: ":disciplineId(\\d+)",
            meta: { requiresAuth: true },
            component: () => import("@/pages/disciplines/DisciplineDetail.vue"),
          },
          {
            name: RouteNames.DISCIPLINE_CREATE,
            path: "create",
            meta: { requiresAuth: true },
            component: () => import("@/pages/disciplines/DisciplineCreate.vue"),
          },
          {
            name: RouteNames.DISCIPLINE_EDIT,
            path: ":disciplineId(\\d+)/edit",
            meta: { requiresAuth: true },
            component: () => import("@/pages/disciplines/DisciplineEdit.vue"),
          },
        ],
      },
      {
        name: LayoutNames.TEAM_DISCIPLINE_LIST,
        path: "teamDisciplines",
        meta: { requiresAuth: true },
        component: () =>
          import("@/pages/teamDisciplines/TeamDisciplineListLayout.vue"),
        children: [
          {
            name: RouteNames.TEAM_DISCIPLINE_LIST,
            path: "",
            meta: { requiresAuth: true },
            component: () =>
              import("@/pages/teamDisciplines/TeamDisciplineNoSelection.vue"),
          },
          {
            name: RouteNames.TEAM_DISCIPLINE_DETAIL,
            path: ":teamDisciplineId(\\d+)",
            meta: { requiresAuth: true },
            component: () =>
              import("@/pages/teamDisciplines/TeamDisciplineDetail.vue"),
          },
          {
            name: RouteNames.TEAM_DISCIPLINE_CREATE,
            path: "create",
            meta: { requiresAuth: true },
            component: () =>
              import("@/pages/teamDisciplines/TeamDisciplineCreate.vue"),
          },
          {
            name: RouteNames.TEAM_DISCIPLINE_EDIT,
            path: ":teamDisciplineId(\\d+)/edit",
            meta: { requiresAuth: true },
            component: () =>
              import("@/pages/teamDisciplines/TeamDisciplineEdit.vue"),
          },
        ],
      },
      {
        name: LayoutNames.TEAM_LIST,
        path: "teams",
        meta: { requiresAuth: true },
        component: () => import("@/pages/teams/TeamListLayout.vue"),
        children: [
          {
            name: RouteNames.TEAM_LIST,
            path: "",
            meta: { requiresAuth: true },
            component: () => import("@/pages/teams/TeamNoSelection.vue"),
          },
          {
            name: RouteNames.TEAM_DETAIL,
            path: ":teamId(\\d+)",
            meta: { requiresAuth: true },
            component: () => import("@/pages/teams/TeamDetail.vue"),
          },
          {
            name: RouteNames.TEAM_CREATE,
            path: "create",
            meta: { requiresAuth: true },
            component: () => import("@/pages/teams/TeamCreate.vue"),
          },
          {
            name: RouteNames.TEAM_EDIT,
            path: ":teamId(\\d+)/edit",
            meta: { requiresAuth: true },
            component: () => import("@/pages/teams/TeamEdit.vue"),
          },
        ],
      },
    ],
  },
];

declare module "vue-router" {
  interface RouteMeta {
    requiresAuth?: boolean;
  }
}
