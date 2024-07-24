package de.anubi1000.turnierverwaltung.test.util

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsNodeInteraction

fun SemanticsNodeInteraction.isSelected(): Boolean = fetchSemanticsNode().config[SemanticsProperties.Selected]