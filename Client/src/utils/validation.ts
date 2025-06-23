import { strings } from "@/utils/strings.ts";
import {
  boolean,
  date,
  enum as zEnum,
  number,
  object,
  string,
  ZodArray,
  ZodNumber,
} from "zod";

const nameSchema = string().refine(
  (name) => name.trim(),
  strings.validation.nameNeeded,
);

function numberSchema(min: number, max: number): ZodNumber {
  return number({ invalid_type_error: strings.validation.numberNeeded })
    .int()
    .min(min)
    .max(max);
}

export const tournamentEditDtoSchema = object({
  name: nameSchema,
  date: date({ message: strings.validation.dateNeeded }),
  teamSize: numberSchema(2, 25),
  //isTeamSizeFixed: boolean(),
});

export const clubEditDtoSchema = object({
  name: nameSchema,
});

export const participantEditDtoSchema = object({
  name: nameSchema,
  startNumber: numberSchema(1, 100_000),
  gender: zEnum(["Male", "Female"]),
  clubId: number().int().min(0, strings.validation.clubNeeded),
});

export const participantResultEditDtoSchema = object({
  rounds: object({
    values: number({
      invalid_type_error: strings.validation.numberNeeded,
    }).array(),
  }).array(),
});

export const disciplineEditDtoSchema = object({
  name: nameSchema,
  amountOfBestRoundsToShow: numberSchema(1, 5),
  areGendersSeparated: boolean(),
  showInResults: boolean(),
  values: object({
    name: nameSchema,
    isAdded: boolean(),
  })
    .array()
    .nonempty(strings.validation.valueNeeded),
});

export const teamDisciplineEditDtoSchema = object({
  name: nameSchema,
  //displayType: zEnum(["Normal", "Nationcup"]),
  basedOn: number().int().array().nonempty(strings.validation.disciplineNeeded),
});

export function getTeamEditDtoSchema(
  teamSize: number,
  isTeamSizeFixed: boolean,
) {
  let membersSchema:
    | ZodArray<ZodNumber, "many">
    | ZodArray<ZodNumber, "atleastone"> = number().int().array();

  if (isTeamSizeFixed) {
    membersSchema = membersSchema.length(
      teamSize,
      strings.validation.exactlyMembersNeeded(teamSize),
    );
  } else {
    membersSchema = membersSchema.nonempty(strings.validation.memberNeeded);
  }

  return object({
    name: nameSchema,
    startNumber: numberSchema(1, 100_000),
    members: membersSchema,
    participatingDisciplines: number().int().array(),
  });
}

export const wordDocGenerationDtoSchema = object({
  tablesToExport: string()
    .nonempty()
    .array()
    .nonempty(strings.status.atLeastOneSelectedTable),
  separateDocuments: boolean(),
});
