using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Turnierverwaltung.Server.Migrations
{
    /// <inheritdoc />
    public partial class Initial : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Tournaments",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false).Annotation("Sqlite:Autoincrement", true),
                    Name = table.Column<string>(type: "TEXT", nullable: false),
                    Date = table.Column<int>(type: "INTEGER", nullable: false),
                    TeamSize = table.Column<int>(type: "INTEGER", nullable: false),
                    IsTeamSizeFixed = table.Column<bool>(type: "INTEGER", nullable: false),
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Tournaments", x => x.Id);
                }
            );

            migrationBuilder.CreateTable(
                name: "Clubs",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false).Annotation("Sqlite:Autoincrement", true),
                    Name = table.Column<string>(type: "TEXT", nullable: false),
                    TournamentId = table.Column<int>(type: "INTEGER", nullable: false),
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Clubs", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Clubs_Tournaments_TournamentId",
                        column: x => x.TournamentId,
                        principalTable: "Tournaments",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade
                    );
                }
            );

            migrationBuilder.CreateTable(
                name: "Disciplines",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false).Annotation("Sqlite:Autoincrement", true),
                    Name = table.Column<string>(type: "TEXT", nullable: false),
                    AmountOfBestRoundsToShow = table.Column<int>(type: "INTEGER", nullable: false),
                    AreGendersSeparated = table.Column<bool>(type: "INTEGER", nullable: false),
                    ShowInResults = table.Column<bool>(type: "INTEGER", nullable: false),
                    TournamentId = table.Column<int>(type: "INTEGER", nullable: false),
                    Values = table.Column<string>(type: "TEXT", nullable: true),
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Disciplines", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Disciplines_Tournaments_TournamentId",
                        column: x => x.TournamentId,
                        principalTable: "Tournaments",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade
                    );
                }
            );

            migrationBuilder.CreateTable(
                name: "TeamDisciplines",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false).Annotation("Sqlite:Autoincrement", true),
                    Name = table.Column<string>(type: "TEXT", nullable: false),
                    DisplayType = table.Column<int>(type: "INTEGER", nullable: false),
                    TournamentId = table.Column<int>(type: "INTEGER", nullable: false),
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_TeamDisciplines", x => x.Id);
                    table.ForeignKey(
                        name: "FK_TeamDisciplines_Tournaments_TournamentId",
                        column: x => x.TournamentId,
                        principalTable: "Tournaments",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade
                    );
                }
            );

            migrationBuilder.CreateTable(
                name: "Teams",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false).Annotation("Sqlite:Autoincrement", true),
                    Name = table.Column<string>(type: "TEXT", nullable: false),
                    StartNumber = table.Column<int>(type: "INTEGER", nullable: false),
                    TournamentId = table.Column<int>(type: "INTEGER", nullable: false),
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Teams", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Teams_Tournaments_TournamentId",
                        column: x => x.TournamentId,
                        principalTable: "Tournaments",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade
                    );
                }
            );

            migrationBuilder.CreateTable(
                name: "Participants",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false).Annotation("Sqlite:Autoincrement", true),
                    Name = table.Column<string>(type: "TEXT", nullable: false),
                    StartNumber = table.Column<int>(type: "INTEGER", nullable: false),
                    Gender = table.Column<int>(type: "INTEGER", nullable: false),
                    TournamentId = table.Column<int>(type: "INTEGER", nullable: false),
                    ClubId = table.Column<int>(type: "INTEGER", nullable: false),
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Participants", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Participants_Clubs_ClubId",
                        column: x => x.ClubId,
                        principalTable: "Clubs",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade
                    );
                    table.ForeignKey(
                        name: "FK_Participants_Tournaments_TournamentId",
                        column: x => x.TournamentId,
                        principalTable: "Tournaments",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade
                    );
                }
            );

            migrationBuilder.CreateTable(
                name: "DisciplineTeamDiscipline",
                columns: table => new
                {
                    BasedOnId = table.Column<int>(type: "INTEGER", nullable: false),
                    UsedInId = table.Column<int>(type: "INTEGER", nullable: false),
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_DisciplineTeamDiscipline", x => new { x.BasedOnId, x.UsedInId });
                    table.ForeignKey(
                        name: "FK_DisciplineTeamDiscipline_Disciplines_BasedOnId",
                        column: x => x.BasedOnId,
                        principalTable: "Disciplines",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade
                    );
                    table.ForeignKey(
                        name: "FK_DisciplineTeamDiscipline_TeamDisciplines_UsedInId",
                        column: x => x.UsedInId,
                        principalTable: "TeamDisciplines",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade
                    );
                }
            );

            migrationBuilder.CreateTable(
                name: "TeamTeamDiscipline",
                columns: table => new
                {
                    ParticipatingDisciplinesId = table.Column<int>(type: "INTEGER", nullable: false),
                    ParticipatingTeamsId = table.Column<int>(type: "INTEGER", nullable: false),
                },
                constraints: table =>
                {
                    table.PrimaryKey(
                        "PK_TeamTeamDiscipline",
                        x => new { x.ParticipatingDisciplinesId, x.ParticipatingTeamsId }
                    );
                    table.ForeignKey(
                        name: "FK_TeamTeamDiscipline_TeamDisciplines_ParticipatingDisciplinesId",
                        column: x => x.ParticipatingDisciplinesId,
                        principalTable: "TeamDisciplines",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade
                    );
                    table.ForeignKey(
                        name: "FK_TeamTeamDiscipline_Teams_ParticipatingTeamsId",
                        column: x => x.ParticipatingTeamsId,
                        principalTable: "Teams",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade
                    );
                }
            );

            migrationBuilder.CreateTable(
                name: "ParticipantResults",
                columns: table => new
                {
                    ParticipantId = table.Column<int>(type: "INTEGER", nullable: false),
                    DisciplineId = table.Column<int>(type: "INTEGER", nullable: false),
                    Rounds = table.Column<string>(type: "TEXT", nullable: true),
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ParticipantResults", x => new { x.ParticipantId, x.DisciplineId });
                    table.ForeignKey(
                        name: "FK_ParticipantResults_Disciplines_DisciplineId",
                        column: x => x.DisciplineId,
                        principalTable: "Disciplines",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade
                    );
                    table.ForeignKey(
                        name: "FK_ParticipantResults_Participants_ParticipantId",
                        column: x => x.ParticipantId,
                        principalTable: "Participants",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade
                    );
                }
            );

            migrationBuilder.CreateTable(
                name: "ParticipantTeam",
                columns: table => new
                {
                    MembersId = table.Column<int>(type: "INTEGER", nullable: false),
                    TeamsId = table.Column<int>(type: "INTEGER", nullable: false),
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ParticipantTeam", x => new { x.MembersId, x.TeamsId });
                    table.ForeignKey(
                        name: "FK_ParticipantTeam_Participants_MembersId",
                        column: x => x.MembersId,
                        principalTable: "Participants",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade
                    );
                    table.ForeignKey(
                        name: "FK_ParticipantTeam_Teams_TeamsId",
                        column: x => x.TeamsId,
                        principalTable: "Teams",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade
                    );
                }
            );

            migrationBuilder.CreateIndex(name: "IX_Clubs_TournamentId", table: "Clubs", column: "TournamentId");

            migrationBuilder.CreateIndex(
                name: "IX_Disciplines_TournamentId",
                table: "Disciplines",
                column: "TournamentId"
            );

            migrationBuilder.CreateIndex(
                name: "IX_DisciplineTeamDiscipline_UsedInId",
                table: "DisciplineTeamDiscipline",
                column: "UsedInId"
            );

            migrationBuilder.CreateIndex(
                name: "IX_ParticipantResults_DisciplineId",
                table: "ParticipantResults",
                column: "DisciplineId"
            );

            migrationBuilder.CreateIndex(name: "IX_Participants_ClubId", table: "Participants", column: "ClubId");

            migrationBuilder.CreateIndex(
                name: "IX_Participants_TournamentId",
                table: "Participants",
                column: "TournamentId"
            );

            migrationBuilder.CreateIndex(
                name: "IX_ParticipantTeam_TeamsId",
                table: "ParticipantTeam",
                column: "TeamsId"
            );

            migrationBuilder.CreateIndex(
                name: "IX_TeamDisciplines_TournamentId",
                table: "TeamDisciplines",
                column: "TournamentId"
            );

            migrationBuilder.CreateIndex(name: "IX_Teams_TournamentId", table: "Teams", column: "TournamentId");

            migrationBuilder.CreateIndex(
                name: "IX_TeamTeamDiscipline_ParticipatingTeamsId",
                table: "TeamTeamDiscipline",
                column: "ParticipatingTeamsId"
            );
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(name: "DisciplineTeamDiscipline");

            migrationBuilder.DropTable(name: "ParticipantResults");

            migrationBuilder.DropTable(name: "ParticipantTeam");

            migrationBuilder.DropTable(name: "TeamTeamDiscipline");

            migrationBuilder.DropTable(name: "Disciplines");

            migrationBuilder.DropTable(name: "Participants");

            migrationBuilder.DropTable(name: "TeamDisciplines");

            migrationBuilder.DropTable(name: "Teams");

            migrationBuilder.DropTable(name: "Clubs");

            migrationBuilder.DropTable(name: "Tournaments");
        }
    }
}
