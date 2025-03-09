using System.Diagnostics.CodeAnalysis;
using FluentAssertions;
using FluentAssertions.Execution;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Endpoints;
using Turnierverwaltung.Server.Model.Transfer;
using Turnierverwaltung.Server.Model.Transfer.Tournament;
using Turnierverwaltung.Server.Tests.Utils;

namespace Turnierverwaltung.Server.Tests.Endpoints.Tournaments;

public class GetTournamentTests : IDisposable
{
    private readonly ApplicationDbContext _dbContext = DbTestUtils.GetInMemoryApplicationDbContext();

    [SuppressMessage("Usage", "CA1816")]
    public void Dispose()
    {
        _dbContext.Database.GetDbConnection().Dispose();
        _dbContext.Dispose();
    }

    [Fact]
    public async Task WhenTournamentExists_ReturnsOkWithTournamentDetails()
    {
        var tournament = new Tournament
        {
            Id = 1,
            Name = "Test Tournament",
            Date = DateTestUtils.GetTestDate(),
            TeamSize = 3,
            Clubs = { new Club { Id = 2 } },
            Disciplines = { new Discipline() },
            TeamDisciplines = { new TeamDiscipline() },
            Participants =
            {
                new Participant { ClubId = 2 },
                new Participant { ClubId = 2 },
                new Participant { ClubId = 2 },
            },
            Teams = { new Team(), new Team(), new Team(), new Team() },
        };

        _dbContext.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var result = await TournamentEndpoints.GetTournament(_dbContext, 1);
        result
            .Should()
            .BeOfType<Ok<TournamentDetailDto>>()
            .Which.Value.Should()
            .NotBeNull()
            .And.Satisfy<TournamentDetailDto>(dto =>
            {
                dto.Id.Should().Be(tournament.Id);
                dto.Name.Should().Be(tournament.Name);
                dto.Date.Should().Be(tournament.Date);
                dto.TeamSize.Should().Be(tournament.TeamSize);
                dto.ClubCount.Should().Be(tournament.Clubs.Count);
                dto.DisciplineCount.Should().Be(tournament.Disciplines.Count + tournament.TeamDisciplines.Count);
                dto.ParticipantCount.Should().Be(tournament.Participants.Count);
                dto.TeamCount.Should().Be(tournament.Teams.Count);
            });
    }

    [Fact]
    public async Task WhenTournamentDoesNotExist_ReturnsNotFound()
    {
        var result = await TournamentEndpoints.GetTournament(_dbContext, 1);
        result.Should().BeOfType<NotFound>();
    }
}
