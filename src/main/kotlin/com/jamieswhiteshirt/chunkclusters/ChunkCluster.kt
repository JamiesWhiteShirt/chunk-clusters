package com.jamieswhiteshirt.chunkclusters

data class ChunkCluster(val count: Int, val center: BlockPos) {
    fun print() {
        println("\tCount: $count, MeanX: ${center.x}, MeanZ: ${center.z}")
    }
}
