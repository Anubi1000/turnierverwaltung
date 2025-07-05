using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Turnierverwaltung.Server.Migrations
{
    /// <inheritdoc />
    public partial class TeamFix : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(name: "FK_Participants_Teams_TeamId", table: "Participants");

            migrationBuilder.DropIndex(name: "IX_Participants_TeamId", table: "Participants");

            migrationBuilder.DropColumn(name: "TeamId", table: "Participants");

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

            migrationBuilder.CreateIndex(
                name: "IX_ParticipantTeam_TeamsId",
                table: "ParticipantTeam",
                column: "TeamsId"
            );
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(name: "ParticipantTeam");

            migrationBuilder.AddColumn<int>(name: "TeamId", table: "Participants", type: "INTEGER", nullable: true);

            migrationBuilder.CreateIndex(name: "IX_Participants_TeamId", table: "Participants", column: "TeamId");

            migrationBuilder.AddForeignKey(
                name: "FK_Participants_Teams_TeamId",
                table: "Participants",
                column: "TeamId",
                principalTable: "Teams",
                principalColumn: "Id"
            );
        }
    }
}
