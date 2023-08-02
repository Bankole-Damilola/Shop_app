package com.example.shopiko01.models

import android.icu.text.Collator.ReorderCodes.PUNCTUATION

private val PUNCTUATION = listOf(", ", "; ", ": ", " ")
/**
 * Truncate long text with a preference for
 * word boundaries and without trailing punctuation.
 */
fun String.smartTruncate(length: Int): String {
    val words = split(" ")
    var added = 0
    var hasMore = false
    val builder = StringBuilder()
    for (word in words) {
        if (builder.length > length) {
            hasMore = true
            break
        }
        builder.append(word)
        builder.append(" ")
        added += 1
    }

    com.example.shopiko01.models.PUNCTUATION.map {
            if (builder.endsWith(it)) {
                builder.replace(builder.length - it.length, builder.length, "")
            }
    }
    if (hasMore) {
        builder.append("...")
    }
    return builder.toString()
}