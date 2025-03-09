import { z } from "zod";

const nameSchema = z
  .string({ message: "Es muss ein Name angegeben werden" })
  .refine((name) => name.trim(), "Der Name darf nicht leer sein");

function numberSchema(min: number, max: number): z.ZodNumber {
  return z
    .number({ invalid_type_error: "Es muss eine Zahl angegeben werden" })
    .int()
    .min(min)
    .max(max);
}

export const tournamentEditSchema = z.object({
  name: nameSchema,
  date: z.date({ message: "Es muss ein Datum angegeben werden" }),
  teamSize: numberSchema(2, 25),
});

export const clubEditSchema = z.object({
  name: nameSchema,
});

export const disciplineEditSchema = z.object({
  name: nameSchema,
  amountOfBestRoundsToShow: numberSchema(1, 5),
  areGendersSeparated: z.boolean(),
  values: z
    .object({
      name: nameSchema,
      isAdded: z.boolean(),
    })
    .array()
    .nonempty("Es muss mindestens einen Wert geben"),
});

export const participantEditSchema = z.object({
  name: nameSchema,
  startNumber: numberSchema(1, 100_000),
  gender: z.enum(["Male", "Female"]),
  clubId: z
    .number({ required_error: "Es muss ein Verein angegeben werden" })
    .int(),
});

export const participantRoundEditSchema = z.object({
  rounds: z
    .object({
      values: z
        .number({
          invalid_type_error: "Es muss eine gültige Zahl eingegeben werden",
        })
        .array(),
    })
    .array(),
});

export const teamDisciplineEditSchema = z.object({
  name: nameSchema,
  basedOn: z
    .number()
    .int()
    .array()
    .nonempty("Es muss mindestens eine Disziplin angegeben werden"),
});
