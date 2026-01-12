package com.tagger.phototagger.data.util

import android.net.Uri
import java.io.File

fun deriveTitleFromUri(uri: Uri): String {
    val derivedTitle = uri.lastPathSegment?.substringAfterLast('/')?.substringBeforeLast('.') ?: ""
    return derivedTitle.ifBlank { "IMG_${System.currentTimeMillis()}" }
}

fun sanitizeFilename(raw: String): String =
    raw.trim().replace(Regex("""[^\w\-. ]"""), "_").take(120)

fun String.hasScheme(): Boolean =
    startsWith("content://") ||
            startsWith("file://") ||
            startsWith("android.resource://") ||
            startsWith("http://") ||
            startsWith("https://")

fun String.toUriFlexible(): Uri =
    if (hasScheme()) Uri.parse(this) else Uri.fromFile(File(this))