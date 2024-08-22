"use client";
import * as React from 'react';
import Stack from "@mui/material/Stack";
import { Typography } from "@mui/material";
import { useTheme } from '@mui/material/styles';

export default function DisciplineModal(
    {
        label,
        table
    }: {
        label: string,
        table: React.ReactNode
    }
) {
    const theme = useTheme();

    return (
        <>
            <Stack
                direction="row"
                justifyContent="center"
                alignItems="center"
                sx={{ width: 1 }}
            >
                <Typography 
                    variant="h5"
                    align="center"
                    sx={{ 
                        backgroundColor: theme.palette.primary.main,
                        color: theme.palette.common.white,
                        width: 1
                    }}
                >
                    {label}
                </Typography>
            </Stack>   
            {table}
      </>
      );
}