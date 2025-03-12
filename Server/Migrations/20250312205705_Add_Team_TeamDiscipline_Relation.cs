using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Turnierverwaltung.Server.Migrations
{
    /// <inheritdoc />
    public partial class Add_Team_TeamDiscipline_Relation : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(name: "FK_TeamDisciplines_Teams_TeamId", table: "TeamDisciplines");

            migrationBuilder.DropIndex(name: "IX_TeamDisciplines_TeamId", table: "TeamDisciplines");

            migrationBuilder.DropColumn(name: "TeamId", table: "TeamDisciplines");

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

            migrationBuilder.CreateIndex(
                name: "IX_TeamTeamDiscipline_ParticipatingTeamsId",
                table: "TeamTeamDiscipline",
                column: "ParticipatingTeamsId"
            );
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(name: "TeamTeamDiscipline");

            migrationBuilder.AddColumn<int>(name: "TeamId", table: "TeamDisciplines", type: "INTEGER", nullable: true);

            migrationBuilder.CreateIndex(name: "IX_TeamDisciplines_TeamId", table: "TeamDisciplines", column: "TeamId");

            migrationBuilder.AddForeignKey(
                name: "FK_TeamDisciplines_Teams_TeamId",
                table: "TeamDisciplines",
                column: "TeamId",
                principalTable: "Teams",
                principalColumn: "Id"
            );
        }
    }
}
