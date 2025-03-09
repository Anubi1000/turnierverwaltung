using System.Diagnostics.CodeAnalysis;
using FluentAssertions;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Endpoints;
using Turnierverwaltung.Server.Model.Transfer.Participant;
using Turnierverwaltung.Server.Tests.Utils;

namespace Turnierverwaltung.Server.Tests.Endpoints.Participants;

public class GetParticipantsTests : IDisposable
{
    private readonly ApplicationDbContext _dbContext = DbTestUtils.GetInMemoryApplicationDbContext();

    [SuppressMessage("Usage", "CA1816")]
    public void Dispose()
    {
        _dbContext.Database.GetDbConnection().Dispose();
        _dbContext.Dispose();
    }

    [Fact]
    public async Task WhenParticipantsExist_ReturnsOkWithParticipantList()
    {
        var tournament = new Tournament
        {
            Id = 1,
            Name = "Test Tournament",
            Clubs =
            {
                new Club { Id = 1, Name = "Test Club" },
            },
            Participants =
            {
                new Participant
                {
                    Id = 1,
                    Name = "Participant 1",
                    StartNumber = 2,
                    ClubId = 1,
                },
                new Participant
                {
                    Id = 2,
                    Name = "Participant 2",
                    StartNumber = 1,
                    ClubId = 1,
                },
                new Participant
                {
                    Id = 3,
                    Name = "Participant 3",
                    StartNumber = 2,
                    ClubId = 1,
                },
            },
        };

        _dbContext.Tournaments.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var result = await ParticipantEndpoints.GetParticipants(_dbContext, 1);
        result
            .Should()
            .BeOfType<Ok<List<ListParticipantDto>>>()
            .Which.Value.Should()
            .NotBeNull()
            .And.HaveSameCount(tournament.Participants)
            .And.BeInAscendingOrder(p => p.StartNumber)
            .And.ThenBeInAscendingOrder(p => p.Name)
            .And.ThenBeInAscendingOrder(p => p.Id);
    }

    [Fact]
    public async Task WhenNoParticipantsExist_ReturnsEmptyList()
    {
        var tournament1 = new Tournament { Id = 1, Name = "Tournament 1" };
        var tournament2 = new Tournament
        {
            Id = 2,
            Name = "Tournament 2",
            Clubs =
            {
                new Club { Id = 1, Name = "Test Club" },
            },
            Participants =
            {
                new Participant
                {
                    Id = 1,
                    Name = "Test Participant",
                    StartNumber = 1,
                    ClubId = 1,
                },
            },
        };

        _dbContext.Tournaments.AddRange(tournament1, tournament2);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var result = await ParticipantEndpoints.GetParticipants(_dbContext, 1);
        result.Should().BeOfType<Ok<List<ListParticipantDto>>>().Which.Value.Should().NotBeNull().And.BeEmpty();
    }

    [Fact]
    public async Task WhenTournamentDoesNotExist_ReturnsNotFound()
    {
        var result = await ParticipantEndpoints.GetParticipants(_dbContext, 1);
        result.Should().BeOfType<NotFound>();
    }
}
