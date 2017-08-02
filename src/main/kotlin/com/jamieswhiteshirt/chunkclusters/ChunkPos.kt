package com.jamieswhiteshirt.chunkclusters

data class ChunkPos(val x: Int, val z: Int) : Comparable<ChunkPos> {
    override fun compareTo(other: ChunkPos): Int {
        return if (this.x == other.x) {
            this.x.compareTo(other.x)
        } else {
            this.z.compareTo(other.z)
        }
    }

    operator fun plus(other: ChunkPos): ChunkPos {
        return ChunkPos(this.x + other.x, this.z + other.z)
    }
}