package com.tagger.phototagger.data.local

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

class LocalFileManager @Inject constructor(@ApplicationContext private val context: Context) {

    fun storeImage(imagePath: String, filename: String): String? {
        val uri = imagePath.toUri()
        val targetDir = File(context.filesDir, "images").apply { mkdirs() }
        val safeName = filename.replace(Regex("""[^\w\-. ]"""), "_").take(120) + ".jpg"
        val target = File(targetDir, safeName)

        val input: InputStream? = when (uri.scheme) {
            ContentResolver.SCHEME_CONTENT, "android.resource" -> context.contentResolver.openInputStream(uri)
            ContentResolver.SCHEME_FILE -> uri.path?.let { File(it).inputStream() }
            else -> null
        }

        input ?: return null

        input.use { ins ->
            FileOutputStream(target).use { outs ->
                val buf = ByteArray(DEFAULT_BUFFER_SIZE)
                while (true) {
                    val r = ins.read(buf)
                    if (r <= 0) break
                    outs.write(buf, 0, r)
                }
            }
        }
        return target.absolutePath
    }
}