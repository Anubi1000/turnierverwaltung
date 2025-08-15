using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Endpoints;
using Turnierverwaltung.Server.Tests.Utils;
using AwesomeAssertions;


namespace Turnierverwaltung.Server.Tests.Endpoints.Participants;

public class DeleteParticipantTests : IDisposable
{
    private readonly ApplicationDbContext _dbContext = DbTestUtils.GetInMemoryApplicationDbContext();

    public void Dispose()
    {
        _dbContext.Database.GetDbConnection().Dispose();
        _dbContext.Dispose();
    }

    [Fact]
    public async Task WhenParticipantExists_ReturnsOk()
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
                    StartNumber = 1,
                    ClubId = 1,
                },

            },
        };

        _dbContext.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        var result = await ParticipantEndpoints.DeleteParticipant(_dbContext, NoOpScoreboardManager.Instance, 1);

        result.Should().BeResult<Results<NotFound, Ok>, Ok>();
    }

    [Fact]
    public async Task WhenParticipantDoesNotExist_ReturnsNotFound()
    {
        var result = await ParticipantEndpoints.DeleteParticipant(_dbContext, NoOpScoreboardManager.Instance, 1);
        result.Should().BeResult<Results<NotFound, Ok>, NotFound>();
    }

    
}