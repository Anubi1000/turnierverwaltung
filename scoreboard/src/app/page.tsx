import { AppBar, Toolbar, Typography } from "@mui/material";
import Stack from "@mui/material/Stack";

import Clock from "./clock";
import Table from "./table";

export default function Home() {
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

      <Stack
        direction="row"
        justifyContent="center"
        alignItems="center"
        sx={{ width: 1 }}
      >
        <Typography variant="h5">Name der Disziplin</Typography>
      </Stack>

      <Table />
    </Stack>
  );
}
