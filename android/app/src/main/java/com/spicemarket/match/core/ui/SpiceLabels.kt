package com.spicemarket.match.core.ui

import com.spicemarket.match.R
import com.spicemarket.match.domain.model.Spice

fun spiceNameRes(spice: Spice): Int = when (spice) {
    Spice.SAFFRON -> R.string.spice_saffron
    Spice.PAPRIKA -> R.string.spice_paprika
    Spice.CINNAMON -> R.string.spice_cinnamon
    Spice.CUMIN -> R.string.spice_cumin
    Spice.CARDAMOM -> R.string.spice_cardamom
}

fun spiceFactRes(spice: Spice): Int = when (spice) {
    Spice.SAFFRON -> R.string.fact_saffron
    Spice.PAPRIKA -> R.string.fact_paprika
    Spice.CINNAMON -> R.string.fact_cinnamon
    Spice.CUMIN -> R.string.fact_cumin
    Spice.CARDAMOM -> R.string.fact_cardamom
}

fun decorNameRes(id: String): Int = when (id) {
    "striped_awning" -> R.string.decor_striped_awning
    "lace_awning" -> R.string.decor_lace_awning
    "brass_lamp" -> R.string.decor_brass_lamp
    "paper_lantern" -> R.string.decor_paper_lantern
    "rope_garland" -> R.string.decor_rope_garland
    else -> R.string.decor_spice_sack
}
