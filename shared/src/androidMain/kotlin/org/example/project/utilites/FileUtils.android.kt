package org.example.project.utilites

import java.io.File

actual fun readAudioFileBytes(path: String): ByteArray {
    return File(path).readBytes()
}
