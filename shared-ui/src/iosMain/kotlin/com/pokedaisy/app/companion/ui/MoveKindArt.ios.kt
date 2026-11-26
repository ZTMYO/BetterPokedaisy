package com.pokedaisy.app.companion.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.pokedaisy.app.companion.data.MoveKind

/** iOS has no Android drawables here: the pixel-art fallback in [MoveKindIcon] draws instead. */
@Composable
actual fun moveKindPainter(kind: MoveKind): Painter? = null
