import {AppBar, Button, Toolbar, Typography} from "@mui/material";

export default function Home() {
  return (
      <AppBar position="static" elevation={0}>
        <Toolbar>
            <Typography variant="h6">
                Scoreboard
            </Typography>
        </Toolbar>
      </AppBar>
  );
}
