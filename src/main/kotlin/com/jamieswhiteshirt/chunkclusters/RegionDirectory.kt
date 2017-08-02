package com.jamieswhiteshirt.chunkclusters

import java.io.File

class RegionDirectory(val directory: File) {
    fun readChunks(): List<ChunkPos> {
        return directory.listFiles().mapNotNull { file ->
            val nameMatch = regionFilePattern.find(file.name)
            if (nameMatch != null) {
                val (_, x, z) = nameMatch.groupValues
                RegionFile(file, RegionFilePos(Integer.parseInt(x, 10), Integer.parseInt(z, 10)))
            } else {
                null
            }
        }.map(RegionFile::readChunks).flatten()
    }
}