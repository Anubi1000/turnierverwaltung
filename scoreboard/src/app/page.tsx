import {AppBar, Button, Container, Toolbar, Typography} from "@mui/material";
import Stack from '@mui/material/Stack';


import Clock from "./clock";
import Table from "./table";

export default function Home() {
  return (
    <>
      <AppBar position="static" elevation={0}>
        <Toolbar>
          <Stack 
            direction="row" 
            justifyContent="space-between" 
            alignItems="center" 
            sx={{ width: 1 }}
          >
            <Typography variant="h6">
              Turniername hier
            </Typography>
            <Typography variant="h6">
              <Clock/>
            </Typography>
          </Stack>
        </Toolbar>
      </AppBar>
      <Container 
        maxWidth={false}
        sx={{ 
          border: 1,
          borderRadius: 2
        }}
      >
          <Table/>
      </Container> 
    </>
  );
}
