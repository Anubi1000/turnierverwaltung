import { queryOptions } from "@tanstack/vue-query";
import type { FetchError } from "ofetch";

export class QueryKeys {
  static tournaments() {
    return ["tournaments"];
  }

  static tournament(tournamentId: number) {
    return ["tournament", tournamentId];
  }

  static clubs(tournamentId: number) {
    return ["clubs", tournamentId];
  }

  static club(clubId: number) {
    return ["club", clubId];
  }

  static disciplines(tournamentId: number) {
    return ["disciplines", tournamentId];
  }

  static discipline(disciplineId: number) {
    return ["discipline", disciplineId];
  }

  static participants(tournamentId: number) {
    return ["participants", tournamentId];
  }

  static participant(participantId: number) {
    return ["participant", participantId];
  }

  static participantResults(participantId: number, disciplineId: number) {
    return ["participantResults", participantId, disciplineId];
  }

  static teamDisciplines(tournamentId: number) {
    return ["team_disciplines", tournamentId];
  }

  static teamDiscipline(teamDisciplineId: number) {
    return ["team_discipline", teamDisciplineId];
  }
}

export class QueryOptions {
  private static listStaleTime = 1000 * 60; // 1min
  private static detailStaleTime = 1000 * 30; // 30s

  static tournaments() {
    return queryOptions<ListTournamentDto[], FetchError>({
      queryKey: QueryKeys.tournaments(),
      queryFn: () => $fetch("/api/tournaments"),
      staleTime: this.listStaleTime,
    });
  }

  static tournament(tournamentId: number) {
    return queryOptions<TournamentDetailDto, FetchError>({
      queryKey: QueryKeys.tournament(tournamentId),
      queryFn: () => $fetch(`/api/tournaments/${tournamentId}`),
      staleTime: this.detailStaleTime,
    });
  }

  static clubs(tournamentId: number) {
    return queryOptions<ListClubDto[], FetchError>({
      queryKey: QueryKeys.clubs(tournamentId),
      queryFn: () => $fetch(`/api/tournaments/${tournamentId}/clubs`),
      staleTime: this.listStaleTime,
    });
  }

  static club(clubId: number) {
    return queryOptions<ClubDetailDto, FetchError>({
      queryKey: QueryKeys.club(clubId),
      queryFn: () => $fetch(`/api/clubs/${clubId}`),
      staleTime: this.detailStaleTime,
    });
  }

  static disciplines(tournamentId: number) {
    return queryOptions<ListDisciplineDto[], FetchError>({
      queryKey: QueryKeys.disciplines(tournamentId),
      queryFn: () => $fetch(`/api/tournaments/${tournamentId}/disciplines`),
      staleTime: this.listStaleTime,
    });
  }

  static discipline(disciplineId: number) {
    return queryOptions<DisciplineDetailDto, FetchError>({
      queryKey: QueryKeys.discipline(disciplineId),
      queryFn: () => $fetch(`/api/disciplines/${disciplineId}`),
      staleTime: this.detailStaleTime,
    });
  }

  static participants(tournamentId: number) {
    return queryOptions<ListParticipantDto[], FetchError>({
      queryKey: QueryKeys.participants(tournamentId),
      queryFn: () => $fetch(`/api/tournaments/${tournamentId}/participants`),
      staleTime: this.listStaleTime,
    });
  }

  static participant(participantId: number) {
    return queryOptions<ParticipantDetailDto, FetchError>({
      queryKey: QueryKeys.participant(participantId),
      queryFn: () => $fetch(`/api/participants/${participantId}`),
      staleTime: this.detailStaleTime,
    });
  }

  static participantResults(participantId: number, disciplineId: number) {
    return queryOptions<ParticipantResultDetailDto, FetchError>({
      queryKey: QueryKeys.participantResults(participantId, disciplineId),
      queryFn: () =>
        $fetch(`/api/participants/${participantId}/results/${disciplineId}`),
      staleTime: this.detailStaleTime,
    });
  }

  static teamDisciplines(tournamentId: number) {
    return queryOptions<ListTeamDisciplineDto[], FetchError>({
      queryKey: QueryKeys.teamDisciplines(tournamentId),
      queryFn: () =>
        $fetch(`/api/tournaments/${tournamentId}/team_disciplines`),
      staleTime: this.listStaleTime,
    });
  }

  static teamDiscipline(teamDisciplineId: number) {
    return queryOptions<TeamDisciplineDetailDto, FetchError>({
      queryKey: QueryKeys.teamDiscipline(teamDisciplineId),
      queryFn: () => $fetch(`/api/team_disciplines/${teamDisciplineId}`),
      staleTime: this.detailStaleTime,
    });
  }
}
