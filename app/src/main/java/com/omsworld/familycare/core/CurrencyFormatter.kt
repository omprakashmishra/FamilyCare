package com.omsworld.familycare.core

import java.text.NumberFormat
import java.util.Locale

/**
 * Simple currency formatter. Replaces CurrencyFormatter.java.
 */
object CurrencyFormatter {

    fun format(amount: Double, locale: Locale = Locale("en", "IN")): String =
        NumberFormat.getCurrencyInstance(locale).format(amount)

    fun formatINR(amount: Double): String =
        NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(amount)

    fun formatUSD(amount: Double): String =
        NumberFormat.getCurrencyInstance(Locale.US).format(amount)
}