"use client";
import { AppBar, Stack, Toolbar, Typography } from "@mui/material";
import { Clock } from "@/app/clock";
import { DisciplineCarousel } from "@/app/discliplineCarousel";

export default function Page() {
  return (
    <Stack direction="column" height="100vh">
      <AppBar position="static" elevation={0}>
        <Toolbar>
          <Stack
            direction="row"
            justifyContent="space-between"
            alignItems="center"
            sx={{ width: 1 }}
          >
            <Typography variant="h6">Turniername hier</Typography>
            <Typography variant="h6">
              <Clock />
            </Typography>
          </Stack>
        </Toolbar>
      </AppBar>

      <DisciplineCarousel />
    </Stack>
  );
}
