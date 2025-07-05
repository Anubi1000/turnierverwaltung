using System.Diagnostics.CodeAnalysis;
using AwesomeAssertions;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Endpoints;
using Turnierverwaltung.Server.Model.Transfer.Club;
using Turnierverwaltung.Server.Tests.Utils;

namespace Turnierverwaltung.Server.Tests.Endpoints.Clubs;

public class GetClubTests : IDisposable
{
    private readonly ApplicationDbContext _dbContext = DbTestUtils.GetInMemoryApplicationDbContext();

    [SuppressMessage("Usage", "CA1816")]
    public void Dispose()
    {
        _dbContext.Database.GetDbConnection().Dispose();
        _dbContext.Dispose();
    }

    [Fact]
    public async Task WhenClubExists_ReturnsOkWithClubDetails()
    {
        var tournament = new Tournament
        {
            Id = 1,
            Clubs =
            {
                new Club { Id = 1, Name = "Test Club" },
            },
            Participants =
            {
                new Participant { Id = 1, ClubId = 1 },
                new Participant { Id = 2, ClubId = 1 },
            },
        };

        _dbContext.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var result = await ClubEndpoints.GetClub(_dbContext, 1);
        result
            .Should()
            .BeResult<Results<NotFound, Ok<ClubDetailDto>>, Ok<ClubDetailDto>>()
            .Subject.Value.Should()
            .NotBeNull()
            .And.Satisfy<ClubDetailDto>(dto =>
            {
                var club = tournament.Clubs.Single();
                var participants = tournament.Participants;

                dto.Id.Should().Be(club.Id);
                dto.TournamentId.Should().Be(tournament.Id);
                dto.Name.Should().Be(club.Name);
                dto.MemberCount.Should().Be(participants.Count);
            });
    }

    [Fact]
    public async Task WhenClubDoesNotExist_ReturnsNotFound()
    {
        var result = await ClubEndpoints.GetClub(_dbContext, 1);
        result.Should().BeResult<Results<NotFound, Ok<ClubDetailDto>>, NotFound>();
    }
}
