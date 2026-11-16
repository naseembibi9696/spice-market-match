package com.spicemarket.match.data.sample

import com.spicemarket.match.domain.model.DecorItem
import com.spicemarket.match.domain.model.DecorKind
import com.spicemarket.match.domain.model.Spice

object SampleData {

    val spices: List<Spice> = listOf(
        Spice.SAFFRON,
        Spice.PAPRIKA,
        Spice.CINNAMON,
        Spice.CUMIN,
        Spice.CARDAMOM
    )

    val decorItems: List<DecorItem> = listOf(
        DecorItem("striped_awning", DecorKind.AWNING, 180),
        DecorItem("lace_awning", DecorKind.AWNING, 240),
        DecorItem("brass_lamp", DecorKind.LAMP, 320),
        DecorItem("paper_lantern", DecorKind.LAMP, 420),
        DecorItem("rope_garland", DecorKind.TRIM, 560),
        DecorItem("spice_sack", DecorKind.TRIM, 700)
    )
}
