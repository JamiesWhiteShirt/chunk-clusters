package com.jamieswhiteshirt.chunkclusters

import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.inf.ArgumentParserException
import java.io.File
import java.util.*

val regionFilePattern = Regex("(-?\\d+)\\.(-?\\d+)\\.mca$")
val chunkOffsets = arrayOf(
        ChunkPos(0, 1),
        ChunkPos(1, 0),
        ChunkPos(0, -1),
        ChunkPos(-1, 0)
)

fun main(args: Array<String>) {
    val parser = ArgumentParsers.newArgumentParser("chunk-clusters").apply {
        description("Tool to analyze Minecraft world chunks")
        addArgument("region").apply {
            help("Path to region folder")
        }
    }

    try {
        val result = parser.parseArgs(args)
        val regionDirectoryFile = File(result.getString("region"))

        if (regionDirectoryFile.isDirectory) {
            val regionDirectory = RegionDirectory(regionDirectoryFile)
            val chunksList = regionDirectory.readChunks()
            val chunkClusters = getChunkClusters(chunksList)

            println("Found ${chunkClusters.size} chunk clusters")
            chunkClusters.forEach { (count, center) ->
                val (x, z) = center
                println("Count: $count, MeanX: $x, MeanZ: $z")
            }
        }
    } catch (e: ArgumentParserException) {
        parser.handleError(e)
    }
}

fun getChunkClusters(chunksList: List<ChunkPos>): List<ChunkCluster> {
    val result = arrayListOf<ChunkCluster>()

    val chunksSet = chunksList.toHashSet()
    while (chunksSet.isNotEmpty()) {
        val islandStack = Stack<ChunkPos>()
        var size = 0
        var x: Long = 8
        var z: Long = 8

        fun consumeIslandChunk(chunk: ChunkPos) {
            if (chunksSet.remove(chunk)) {
                islandStack.push(chunk)
                size++
                x += chunk.x * 16
                z += chunk.z * 16
            }
        }

        consumeIslandChunk(chunksSet.iterator().next())
        while (islandStack.isNotEmpty()) {
            val chunk = islandStack.pop()
            chunkOffsets.map { it + chunk }.forEach(::consumeIslandChunk)
        }

        result.add(ChunkCluster(size, BlockPos((x / size).toInt(), (z / size).toInt())))
    }

    return result
}
