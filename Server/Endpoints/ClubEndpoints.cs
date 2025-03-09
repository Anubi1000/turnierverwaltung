using FluentValidation;
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
        tournamentDependentGroup
            .MapGet("/", GetClubs)
            .Produces<List<ListClubDto>>()
            .Produces(StatusCodes.Status404NotFound);

        tournamentDependentGroup.MapPost("/", CreateClub).Produces<int>().Produces(StatusCodes.Status404NotFound);

        // Club routes
        tournamentIndependentGroup
            .MapGet("/", GetClub)
            .Produces<ClubDetailDto>()
            .Produces(StatusCodes.Status404NotFound);

        tournamentIndependentGroup
            .MapPut("/", UpdateClub)
            .Produces(StatusCodes.Status200OK)
            .Produces(StatusCodes.Status404NotFound);

        tournamentIndependentGroup
            .MapDelete("/", DeleteClub)
            .Produces(StatusCodes.Status200OK)
            .Produces(StatusCodes.Status404NotFound);

        return builder;
    }

    public static async Task<IResult> GetClubs(ApplicationDbContext dbContext, int tournamentId)
    {
        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return Results.NotFound();

        var clubs = await dbContext
            .Clubs.AsNoTracking()
            .Where(c => c.TournamentId == tournamentId)
            .OrderBy(c => c.Name)
            .ThenBy(c => c.Id)
            .Select(c => new ListClubDto(c.Id, c.Name))
            .ToListAsync();

        return Results.Ok(clubs);
    }

    public static async Task<IResult> CreateClub(
        ApplicationDbContext dbContext,
        IValidator<ClubEditDto> validator,
        int tournamentId,
        ClubEditDto dto
    )
    {
        if (!await dbContext.Tournaments.AnyAsync(t => t.Id == tournamentId))
            return Results.NotFound();

        var validationResult = await validator.ValidateAsync(dto);
        if (!validationResult.IsValid)
            return TypedResults.ValidationProblem(validationResult.ToValidationProblemErrors());

        dto = SanitizeEditDto(dto);

        var club = new Club { Name = dto.Name, TournamentId = tournamentId };

        dbContext.Add(club);
        await dbContext.SaveChangesAsync();

        return Results.Ok(club.Id);
    }

    public static async Task<IResult> GetClub(ApplicationDbContext dbContext, int clubId)
    {
        var club = await dbContext
            .Clubs.AsNoTracking()
            .Where(c => c.Id == clubId)
            .Select(c => new ClubDetailDto(
                c.Id,
                c.TournamentId,
                c.Name,
                dbContext.Participants.Count(p => p.ClubId == clubId)
            ))
            .SingleOrDefaultAsync();

        return club is not null ? Results.Ok(club) : Results.NotFound();
    }

    public static async Task<IResult> UpdateClub(
        ApplicationDbContext dbContext,
        IValidator<ClubEditDto> validator,
        int clubId,
        ClubEditDto dto
    )
    {
        var club = await dbContext.Clubs.FindAsync(clubId);
        if (club is null)
            return Results.NotFound();

        var validationResult = await validator.ValidateAsync(dto);
        if (!validationResult.IsValid)
            return TypedResults.ValidationProblem(validationResult.ToValidationProblemErrors());

        dto = SanitizeEditDto(dto);

        club.Name = dto.Name;

        await dbContext.SaveChangesAsync();
        return Results.Ok();
    }

    public static async Task<IResult> DeleteClub(ApplicationDbContext dbContext, int clubId)
    {
        var club = await dbContext.Clubs.FindAsync(clubId);
        if (club is null)
            return Results.NotFound();

        dbContext.Remove(club);
        await dbContext.SaveChangesAsync();

        return Results.Ok();
    }

    private static ClubEditDto SanitizeEditDto(ClubEditDto dto)
    {
        return dto with { Name = dto.Name.Trim() };
    }
}
