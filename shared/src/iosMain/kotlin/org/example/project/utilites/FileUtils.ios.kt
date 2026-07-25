package org.example.project.utilites

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
actual fun readAudioFileBytes(path: String): ByteArray {
    val nsData = NSData.dataWithContentsOfFile(path) ?: return ByteArray(0)
    val byteArray = ByteArray(nsData.length.toInt())
    if (byteArray.isNotEmpty()) {
        byteArray.usePinned {
            memcpy(it.addressOf(0), nsData.bytes, nsData.length)
        }
    }
    return byteArray
}
