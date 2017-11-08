package com.jamieswhiteshirt.chunkclusters

import java.io.File

class RegionDirectory(val directory: File) {
    fun readChunks(): Map<List<StackTraceElement>, Set<ChunkPos>> {
        val result = HashMap<List<StackTraceElement>, MutableSet<ChunkPos>>()
        directory.listFiles().mapNotNull { file ->
            val nameMatch = regionFilePattern.find(file.name)
            if (nameMatch != null) {
                val (_, x, z) = nameMatch.groupValues
                RegionFile(file, RegionFilePos(Integer.parseInt(x, 10), Integer.parseInt(z, 10)))
            } else {
                null
            }
        }.forEach { chunkMap ->
            chunkMap.readChunks().forEach { stackTrace, chunkPositions ->
                result.getOrPut(stackTrace, { HashSet() }).addAll(chunkPositions)
            }
        }
        return result
    }
}