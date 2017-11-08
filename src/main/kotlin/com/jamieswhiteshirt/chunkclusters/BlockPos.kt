package com.jamieswhiteshirt.chunkclusters

data class BlockPos(val x: Int, val z: Int) {
    operator fun plus(other: BlockPos) = BlockPos(this.x + other.x, this.z + other.z)
}
