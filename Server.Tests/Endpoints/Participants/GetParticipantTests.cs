using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Tests.Utils;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Endpoints;
using AwesomeAssertions;
using Microsoft.AspNetCore.Http.HttpResults;
using Turnierverwaltung.Server.Model.Transfer.Participant;
namespace Turnierverwaltung.Server.Tests.Endpoints.Participants;

public class GetParticipantTests : IDisposable
{
    private readonly ApplicationDbContext _dbContext = DbTestUtils.GetInMemoryApplicationDbContext();

    public void Dispose()
    {
        _dbContext.Database.GetDbConnection().Dispose();
        _dbContext.Dispose();
    }

    [Fact]
    public async Task WithValidData_ReturnsOkWithParticipantDetailDto()
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

        var result = await ParticipantEndpoints.GetParticipant(_dbContext, 1);

        result.Should().BeResult<Results<NotFound, Ok<ParticipantDetailDto>>, Ok<ParticipantDetailDto>>();
    }

    [Fact]
    public async Task WhenParticipantDoesNotExist_ReturnsNotFound()
    {
        var result = await ParticipantEndpoints.GetParticipant(_dbContext, 1);
        result.Should().BeResult<Results<NotFound, Ok<ParticipantDetailDto>>, NotFound>();
    }
}