import { queryOptions } from "@tanstack/vue-query";
import type { FetchError } from "ofetch";

const LIST_STALE_TIME = 1000 * 60; // 1 min
const DETAIL_STALE_TIME = 1000 * 30; // 30 sec

export const tournamentsQueryOptions = () => queryOptions<ListTournamentDto[], FetchError>({
  queryKey: tournamentsQueryKey(),
  queryFn: () => $fetch("/api/tournaments"),
  staleTime: LIST_STALE_TIME,
});

export const tournamentQueryOptions = (tournamentId: number) => queryOptions<TournamentDetailDto, FetchError>({
  queryKey: tournamentQueryKey(tournamentId),
  queryFn: () => $fetch(`/api/tournaments/${tournamentId}`),
  staleTime: DETAIL_STALE_TIME,
});

export const clubsQueryOptions = (tournamentId: number) => queryOptions<ListClubDto[], FetchError>({
  queryKey: clubsQueryKey(tournamentId),
  queryFn: () => $fetch(`/api/tournaments/${tournamentId}/clubs`),
  staleTime: LIST_STALE_TIME,
});

export const clubQueryOptions = (clubId: number) => queryOptions<ClubDetailDto, FetchError>({
  queryKey: clubQueryKey(clubId),
  queryFn: () => $fetch(`/api/clubs/${clubId}`),
  staleTime: DETAIL_STALE_TIME,
});

export const disciplinesQueryOptions = (tournamentId: number) => queryOptions<ListDisciplineDto[], FetchError>({
  queryKey: disciplinesQueryKey(tournamentId),
  queryFn: () => $fetch(`/api/tournaments/${tournamentId}/disciplines`),
  staleTime: LIST_STALE_TIME,
});

export const disciplineQueryOptions = (disciplineId: number) => queryOptions<DisciplineDetailDto, FetchError>({
  queryKey: disciplineQueryKey(disciplineId),
  queryFn: () => $fetch(`/api/disciplines/${disciplineId}`),
  staleTime: DETAIL_STALE_TIME,
});

export const participantsQueryOptions = (tournamentId: number) => queryOptions<ListParticipantDto[], FetchError>({
  queryKey: participantsQueryKey(tournamentId),
  queryFn: () => $fetch(`/api/tournaments/${tournamentId}/participants`),
  staleTime: LIST_STALE_TIME,
});

export const participantQueryOptions = (participantId: number) => queryOptions<ParticipantDetailDto, FetchError>({
  queryKey: participantQueryKey(participantId),
  queryFn: () => $fetch(`/api/participants/${participantId}`),
  staleTime: DETAIL_STALE_TIME,
});

export const participantResultsQueryOptions = (participantId: number, disciplineId: number) => queryOptions<ParticipantResultDetailDto, FetchError>({
  queryKey: participantResultsQueryKey(participantId, disciplineId),
  queryFn: () => $fetch(`/api/participants/${participantId}/results/${disciplineId}`),
  staleTime: DETAIL_STALE_TIME,
});

export const teamDisciplinesQueryOptions = (tournamentId: number) => queryOptions<ListTeamDisciplineDto[], FetchError>({
  queryKey: teamDisciplinesQueryKey(tournamentId),
  queryFn: () => $fetch(`/api/tournaments/${tournamentId}/team_disciplines`),
  staleTime: LIST_STALE_TIME,
});

export const teamDisciplineQueryOptions = (teamDisciplineId: number) => queryOptions<TeamDisciplineDetailDto, FetchError>({
  queryKey: teamDisciplineQueryKey(teamDisciplineId),
  queryFn: () => $fetch(`/api/team_disciplines/${teamDisciplineId}`),
  staleTime: DETAIL_STALE_TIME,
});

