/**
 * Generated by orval v7.9.0 🍺
 * Do not edit manually.
 * Turnierverwaltung
 * OpenAPI spec version: 1.0.0
 */
import type { TeamDetailDtoMember } from "./teamDetailDtoMember";
import type { TeamDetailDtoTeamDiscipline } from "./teamDetailDtoTeamDiscipline";

export interface TeamDetailDto {
  name: string;
  startNumber: number;
  members: TeamDetailDtoMember[];
  participatingDisciplines: TeamDetailDtoTeamDiscipline[];
}
