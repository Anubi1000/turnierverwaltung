import type { Metadata } from "next";
import "./globals.css";
import {AppRouterCacheProvider} from "@mui/material-nextjs/v14-appRouter";
import {CssBaseline, ThemeProvider} from "@mui/material";
import theme from "@/theme";

export const metadata: Metadata = {
  title: "Scoreboard"
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
        <AppRouterCacheProvider>
          <CssBaseline/>
          <ThemeProvider theme={theme}>
            {children}
          </ThemeProvider>
        </AppRouterCacheProvider>
      </body>
    </html>
  );
}
