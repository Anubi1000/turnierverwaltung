using System.Diagnostics.CodeAnalysis;
using AwesomeAssertions;
using FluentValidation;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.EntityFrameworkCore;
using Turnierverwaltung.Server.Database;
using Turnierverwaltung.Server.Database.Model;
using Turnierverwaltung.Server.Endpoints;
using Turnierverwaltung.Server.Model.Transfer.Participant;
using Turnierverwaltung.Server.Tests.Utils;
using Turnierverwaltung.Server.Model.Validation;

namespace Turnierverwaltung.Server.Tests.Endpoints.Participants;

public class CreateParticipantsTests : IDisposable
{
    private readonly ApplicationDbContext _dbContext = DbTestUtils.GetInMemoryApplicationDbContext();
    public void Dispose()
    {
        _dbContext.Database.GetDbConnection().Dispose();
        _dbContext.Dispose();
    }

    [Fact]
    public async Task WithValidData_ReturnsOkWithParticipantId()
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
                new Participant
                {
                    Id = 2,
                    Name = "Participant 2",
                    StartNumber = 2,
                    ClubId = 1,
                },
                new Participant
                {
                    Id = 3,
                    Name = "Participant 3",
                    StartNumber = 3,
                    ClubId = 1,
                },
            },
        };

        _dbContext.Tournaments.Add(tournament);
        await _dbContext.SaveChangesAsync(TestContext.Current.CancellationToken);

        IValidator<ParticipantEditDto> _validator = new ParticipantEditDtoValidator(_dbContext);
        ParticipantEditDto dto = new ParticipantEditDto("Participant 4", 4, Gender.Male, 1);

        var result = await ParticipantEndpoints.CreateParticipant(_dbContext, _validator, NoOpScoreboardManager.Instance, 1, dto);
        result
            .Should()
            .BeResult<Results<NotFound, ValidationProblem, Ok<int>>, Ok<int>>()
            .Which.Value.Should().Be(4);
    }
}