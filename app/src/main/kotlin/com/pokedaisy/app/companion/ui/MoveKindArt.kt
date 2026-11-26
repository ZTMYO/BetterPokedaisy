package com.pokedaisy.app.companion.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.pokedaisy.app.companion.data.MoveKind

/** The fork's own physical / special / status PNG, where the platform has Android's resources (iOS falls back). */
@Composable
expect fun moveKindPainter(kind: MoveKind): Painter?
