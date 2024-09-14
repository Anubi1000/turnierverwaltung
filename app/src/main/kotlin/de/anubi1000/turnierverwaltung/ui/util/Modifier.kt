package de.anubi1000.turnierverwaltung.ui.util

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
fun Modifier.topAppBarPadding() = padding(top = TopAppBarDefaults.TopAppBarExpandedHeight)
