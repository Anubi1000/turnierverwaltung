import { Stack, Typography } from "@mui/material";

export function CenteredText({ text }: { text: string }) {
  return (
    <Stack direction="column" justifyContent="center" sx={{ height: 1 }}>
      <Typography variant="h4" align="center">
        {text}
      </Typography>
    </Stack>
  );
}
