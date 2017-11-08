package com.jamieswhiteshirt.chunkclusters

import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.inf.ArgumentParserException
import java.io.File
import java.util.*

val regionFilePattern = Regex("(-?\\d+)\\.(-?\\d+)\\.mca$")
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
            val stackTraceMap = regionDirectory.readChunks()

            val combinedChunkPositions = stackTraceMap.values.reduce { acc, set ->
                acc + set
            }
            println("ALL CHUNKS")
            ChunkPosSetAnalysis(combinedChunkPositions).print()

            println("Discovered ${stackTraceMap.size} unique stack traces")
            println()

            stackTraceMap.map { (stackTrace, chunkPositions) ->
                stackTrace to ChunkPosSetAnalysis(chunkPositions)
            }.sortedByDescending { (_, analysis) ->
                analysis.clusteringScore
            }.forEach { (stackTrace, analysis) ->
                println("LOADED WITH STACK TRACE")
                stackTrace.subList(5, stackTrace.size).forEach {
                    println("\t$it")
                }
                analysis.print()
            }
        }
    } catch (e: ArgumentParserException) {
        parser.handleError(e)
    }
}
