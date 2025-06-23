using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Turnierverwaltung.Server.Migrations
{
    /// <inheritdoc />
    public partial class Discipline_ShowInResults : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<bool>(
                name: "ShowInResults",
                table: "Disciplines",
                type: "INTEGER",
                nullable: false,
                defaultValue: false);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "ShowInResults",
                table: "Disciplines");
        }
    }
}
