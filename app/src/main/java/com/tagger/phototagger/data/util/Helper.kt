package com.tagger.phototagger.data.util

import android.net.Uri

fun deriveTitleFromUri(uri: Uri): String {
    val derivedTitle = uri.lastPathSegment?.substringAfterLast('/')?.substringBeforeLast('.') ?: ""
    return derivedTitle.ifBlank { "IMG_${System.currentTimeMillis()}" }
}

fun sanitizeFilename(raw: String): String =
    raw.trim().replace(Regex("""[^\w\-. ]"""), "_").take(120)