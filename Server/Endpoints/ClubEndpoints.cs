using FluentValidation;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using SharpGrip.FluentValidation.AutoValidation.Shared.Extensions;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Model.Transfer.Club;

namespace Turnierverwaltung.Server.Endpoints;

public static class ClubEndpoints
{
    public static IEndpointRouteBuilder MapClubEndpoints(this IEndpointRouteBuilder builder)
    {
        var baseGroup = builder.MapGroup("/api").WithTags("Club").RequireAuthorization();

        var tournamentDependentGroup = baseGroup.MapGroup("/tournaments/{tournamentId:int}/clubs");
        var tournamentIndependentGroup = baseGroup.MapGroup("/clubs/{clubId:int}");

        // Tournament-based routes
        tournamentDependentGroup.MapGet("/", GetClubs);

        tournamentDependentGroup.MapPost("/", CreateClub);

        // Club routes
        tournamentIndependentGroup.MapGet("/", GetClub);

        tournamentIndependentGroup.MapPut("/", UpdateClub);

        tournamentIndependentGroup.MapDelete("/", DeleteClub);

        return builder;
    }

    public static async Task<Results<NotFound, Ok<List<ListClubDto>>>> GetClubs(
        ApplicationDbContext dbContext,
        int tournamentId
    )
    {
        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return TypedResults.NotFound();

        var clubs = await dbContext
            .Clubs.AsNoTracking()
            .Where(c => c.TournamentId == tournamentId)
            .OrderBy(c => c.Name)
            .ThenBy(c => c.Id)
            .Select(c => new ListClubDto(c.Id, c.Name))
            .ToListAsync();

        return TypedResults.Ok(clubs);
    }

    public static async Task<Results<NotFound, ValidationProblem, Ok<int>>> CreateClub(
        ApplicationDbContext dbContext,
        IValidator<ClubEditDto> validator,
        int tournamentId,
        ClubEditDto dto
    )
    {
        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return TypedResults.NotFound();

        var validationResult = await validator.ValidateAsync(dto);
        if (!validationResult.IsValid)
            return TypedResults.ValidationProblem(validationResult.ToValidationProblemErrors());

        dto = SanitizeEditDto(dto);

        var club = new Club { Name = dto.Name, TournamentId = tournamentId };

        dbContext.Add(club);
        await dbContext.SaveChangesAsync();

        return TypedResults.Ok(club.Id);
    }

    public static async Task<Results<NotFound, Ok<ClubDetailDto>>> GetClub(ApplicationDbContext dbContext, int clubId)
    {
        var club = await dbContext
            .Clubs.AsNoTracking()
            .Where(c => c.Id == clubId)
            .Select(c => new ClubDetailDto(c.Id, c.TournamentId, c.Name, c.Members.Count))
            .SingleOrDefaultAsync();

        return club is null ? TypedResults.NotFound() : TypedResults.Ok(club);
    }

    public static async Task<Results<NotFound, ValidationProblem, Ok>> UpdateClub(
        ApplicationDbContext dbContext,
        IValidator<ClubEditDto> validator,
        int clubId,
        ClubEditDto dto
    )
    {
        var club = await dbContext.Clubs.FindAsync(clubId);
        if (club is null)
            return TypedResults.NotFound();

        var validationResult = await validator.ValidateAsync(dto);
        if (!validationResult.IsValid)
            return TypedResults.ValidationProblem(validationResult.ToValidationProblemErrors());

        dto = SanitizeEditDto(dto);

        club.Name = dto.Name;

        await dbContext.SaveChangesAsync();
        return TypedResults.Ok();
    }

    public static async Task<Results<NotFound, Ok>> DeleteClub(ApplicationDbContext dbContext, int clubId)
    {
        var club = await dbContext.Clubs.FindAsync(clubId);
        if (club is null)
            return TypedResults.NotFound();

        dbContext.Remove(club);
        await dbContext.SaveChangesAsync();

        return TypedResults.Ok();
    }

    private static ClubEditDto SanitizeEditDto(ClubEditDto dto)
    {
        return dto with { Name = dto.Name.Trim() };
    }
}
