namespace Turnierverwaltung.Server.Model.Transfer;

public record WordDocGenerationDto(List<string> TablesToExport, bool SeparateDocuments);
