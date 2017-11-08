package com.jamieswhiteshirt.chunkclusters

import com.flowpowered.nbt.CompoundTag
import com.flowpowered.nbt.IntTag
import com.flowpowered.nbt.ListTag
import com.flowpowered.nbt.StringTag
import com.flowpowered.nbt.stream.NBTInputStream
import java.io.ByteArrayInputStream
import java.io.File
import java.io.RandomAccessFile
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream

private const val SECTOR_BYTES = 4096
private const val VERSION_GZIP = 1
private const val VERSION_DEFLATE = 2

class RegionFile(val file: File, val pos: RegionFilePos) {
    fun readChunks(): Map<List<StackTraceElement>, Set<ChunkPos>> {
        return RandomAccessFile(file, "r").use { raf ->
            val offsets = (0 until 1024).map {
                raf.readInt()
            }

            val result = HashMap<List<StackTraceElement>, MutableSet<ChunkPos>>()
            offsets.forEachIndexed { index, offset ->
                if (offset != 0) {
                    val x = index and 0b11111
                    val z = (index shr 5) and 0b11111
                    val pos = ChunkPos(pos.x * 32 + x, pos.z * 32 + z)

                    val sectorNumber = offset shr 8
                    val numSectors = offset and 0xFF

                    raf.seek((sectorNumber * SECTOR_BYTES).toLong())
                    val length = raf.readInt()
                    val version = raf.read()
                    val data = ByteArray(length - 1)
                    raf.read(data)

                    val innerStream = ByteArrayInputStream(data)
                    val nbtStream = NBTInputStream(when (version) {
                        VERSION_GZIP -> GZIPInputStream(innerStream)
                        VERSION_DEFLATE -> InflaterInputStream(innerStream)
                        else -> innerStream
                    }, false)

                    val tag = nbtStream.readTag() as CompoundTag
                    val level = tag.value["Level"] as CompoundTag
                    val forgeCaps = level.value["ForgeCaps"] as CompoundTag?
                    if (forgeCaps != null) {
                        val wmtcChunkInfo = forgeCaps.value["wmtc:chunk_info"] as ListTag<ListTag<CompoundTag>>?
                        if (wmtcChunkInfo != null) {
                            wmtcChunkInfo.value.map { stackTraceTag ->
                                stackTraceTag.value.map { stackTraceElementTag ->
                                    val className = stackTraceElementTag.value["className"] as StringTag
                                    val methodName = stackTraceElementTag.value["methodName"] as StringTag
                                    val fileName = stackTraceElementTag.value["fileName"] as StringTag
                                    val lineNumber = stackTraceElementTag.value["lineNumber"] as IntTag
                                    StackTraceElement(
                                            className.value,
                                            methodName.value,
                                            fileName.value,
                                            lineNumber.value
                                    )
                                }
                            }.forEach {
                                result.getOrPut(it, { HashSet() }).add(pos)
                            }
                        }
                    }
                }
            }
            result
        }
    }
}
