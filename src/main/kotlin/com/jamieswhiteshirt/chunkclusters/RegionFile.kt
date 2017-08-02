package com.jamieswhiteshirt.chunkclusters

import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream

class RegionFile(val file: File, val pos: RegionFilePos) {
    fun readChunks(): List<ChunkPos> {
        return DataInputStream(FileInputStream(file)).use { stream ->
            (0 until 1024).mapNotNull { i ->
                if (stream.readInt() != 0) {
                    val x = i and 0b11111
                    val z = (i shr 5) and 0b11111
                    ChunkPos(pos.x * 32 + x, pos.z * 32 + z)
                } else {
                    null
                }
            }
        }
    }
}
