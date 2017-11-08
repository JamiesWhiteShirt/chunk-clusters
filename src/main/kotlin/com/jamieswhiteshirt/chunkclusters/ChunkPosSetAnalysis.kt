package com.jamieswhiteshirt.chunkclusters

import java.util.*

class ChunkPosSetAnalysis(private val positions: Set<ChunkPos>) {
    private val clusters = getChunkClusters(positions)
    val clusteringScore get() = positions.size.toFloat() / clusters.size

    fun print() {
        println("Clustering score: $clusteringScore")
        println("${clusters.size} clusters of chunks:")
        clusters.forEach(ChunkCluster::print)
        println("All chunks:")
        val count = clusters.map { it.count }.sum()
        var x: Long = 8
        var z: Long = 8
        positions.forEach {
            x += it.x * 16
            z += it.z * 16
        }
        ChunkCluster(count, BlockPos((x / count).toInt(), (z / count).toInt())).print()
        println()
    }
}

private val chunkOffsets = arrayOf(
        ChunkPos(0, 1),
        ChunkPos(1, 0),
        ChunkPos(0, -1),
        ChunkPos(-1, 0)
)

private fun getChunkClusters(chunkPositions: Set<ChunkPos>): List<ChunkCluster> {
    val result = arrayListOf<ChunkCluster>()

    val chunksSet = chunkPositions.toHashSet()
    while (chunksSet.isNotEmpty()) {
        val islandStack = ArrayDeque<ChunkPos>()
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
