using System.ComponentModel.DataAnnotations;

namespace Turnierverwaltung.Server.Database.Model;

public class Club
{
    public int Id { get; init; }

    [MaxLength(150)]
    public string Name { get; set; } = "";

    public int TournamentId { get; init; }
}
