package com.pokedaisy.app.companion.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.pokedaisy.app.companion.data.MoveKind
import com.pokedaisy.sharedui.R

@Composable
actual fun moveKindPainter(kind: MoveKind): Painter? = when (kind) {
    MoveKind.PHYSICAL -> painterResource(R.drawable.move_physical)
    MoveKind.SPECIAL -> painterResource(R.drawable.move_special)
    MoveKind.STATUS -> painterResource(R.drawable.move_status)
}
