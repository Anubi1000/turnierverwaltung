using FluentValidation;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SharpGrip.FluentValidation.AutoValidation.Shared.Extensions;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Model.Transfer.Club;

namespace Turnierverwaltung.Server.Endpoints;

public static class ClubEndpoints
{
    private static readonly Func<ApplicationDbContext, int, IAsyncEnumerable<ListClubDto>> GetClubsQuery =
        EF.CompileAsyncQuery(
            (ApplicationDbContext dbContext, int tournamentId) =>
                dbContext
                    .Clubs.AsNoTracking()
                    .Where(c => c.TournamentId == tournamentId)
                    .OrderBy(c => c.Name)
                    .ThenBy(c => c.Id)
                    .Select(c => new ListClubDto(c.Id, c.Name))
        );

    private static readonly Func<ApplicationDbContext, int, Task<ClubDetailDto?>> GetClubQuery = EF.CompileAsyncQuery(
        (ApplicationDbContext dbContext, int clubId) =>
            dbContext
                .Clubs.AsNoTracking()
                .Where(c => c.Id == clubId)
                .Select(c => new ClubDetailDto(c.Id, c.TournamentId, c.Name, c.Members.Count))
                .SingleOrDefault()
    );

    public static IEndpointRouteBuilder MapClubEndpoints(this IEndpointRouteBuilder builder)
    {
        var baseGroup = builder.MapGroup("/api").WithTags("Club").RequireAuthorization();

        var tournamentDependentGroup = baseGroup.MapGroup("/tournaments/{tournamentId:int}/clubs");
        var tournamentIndependentGroup = baseGroup.MapGroup("/clubs/{clubId:int}");

        // Tournament-based routes
        tournamentDependentGroup.MapGet("/", GetClubs).WithName("GetClubs");

        tournamentDependentGroup.MapPost("/", CreateClub).WithName("CreateClub");

        // Club routes
        tournamentIndependentGroup.MapGet("/", GetClub).WithName("GetClub");

        tournamentIndependentGroup.MapPut("/", UpdateClub).WithName("UpdateClub");

        tournamentIndependentGroup.MapDelete("/", DeleteClub).WithName("DeleteClub");

        return builder;
    }

    public static async Task<Results<NotFound, Ok<List<ListClubDto>>>> GetClubs(
        [FromServices] ApplicationDbContext dbContext,
        [FromRoute] int tournamentId
    )
    {
        if (!await ApplicationDbContext.ExistsTournamentWithIdQuery(dbContext, tournamentId))
            return TypedResults.NotFound();

        var clubs = await GetClubsQuery(dbContext, tournamentId).ToListAsync();

        return TypedResults.Ok(clubs);
    }

    public static async Task<Results<NotFound, ValidationProblem, Ok<int>>> CreateClub(
        [FromServices] ApplicationDbContext dbContext,
        [FromServices] IValidator<ClubEditDto> validator,
        [FromRoute] int tournamentId,
        [FromBody] ClubEditDto dto
    )
    {
        if (!await ApplicationDbContext.ExistsTournamentWithIdQuery(dbContext, tournamentId))
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

    public static async Task<Results<NotFound, Ok<ClubDetailDto>>> GetClub(
        [FromServices] ApplicationDbContext dbContext,
        [FromRoute] int clubId
    )
    {
        var club = await GetClubQuery(dbContext, clubId);
        return club is null ? TypedResults.NotFound() : TypedResults.Ok(club);
    }

    public static async Task<Results<NotFound, ValidationProblem, Ok>> UpdateClub(
        [FromServices] ApplicationDbContext dbContext,
        [FromServices] IValidator<ClubEditDto> validator,
        [FromRoute] int clubId,
        [FromBody] ClubEditDto dto
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

    public static async Task<Results<NotFound, Ok>> DeleteClub(
        [FromServices] ApplicationDbContext dbContext,
        [FromRoute] int clubId
    )
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
