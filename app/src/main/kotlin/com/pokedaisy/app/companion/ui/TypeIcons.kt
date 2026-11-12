package com.pokedaisy.app.companion.ui

import com.pokedaisy.app.R

/** PokeIdle's type icons (src/index.html's icon-type-* symbols), converted to VectorDrawables. */
internal fun typeIconRes(type: String): Int? = when (type.uppercase()) {
    "NORMAL" -> R.drawable.type_normal
    "FIGHTING" -> R.drawable.type_fighting
    "FLYING" -> R.drawable.type_flying
    "POISON" -> R.drawable.type_poison
    "GROUND" -> R.drawable.type_ground
    "ROCK" -> R.drawable.type_rock
    "BUG" -> R.drawable.type_bug
    "GHOST" -> R.drawable.type_ghost
    "STEEL" -> R.drawable.type_steel
    "FIRE" -> R.drawable.type_fire
    "WATER" -> R.drawable.type_water
    "GRASS" -> R.drawable.type_grass
    "ELECTRIC" -> R.drawable.type_electric
    "PSYCHIC" -> R.drawable.type_psychic
    "ICE" -> R.drawable.type_ice
    "DRAGON" -> R.drawable.type_dragon
    "DARK" -> R.drawable.type_dark
    "FAIRY" -> R.drawable.type_fairy
    else -> null
}
