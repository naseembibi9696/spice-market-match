package com.spicemarket.match.core.util

fun formatNumber(value: Int): String {
    val digits = value.toString()
    if (digits.length <= 3) return digits
    val builder = StringBuilder()
    val reversed = digits.reversed()
    for (index in reversed.indices) {
        if (index > 0 && index % 3 == 0) builder.append(' ')
        builder.append(reversed[index])
    }
    return builder.reverse().toString()
}
