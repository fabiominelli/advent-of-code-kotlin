package aoc24

import Problem
import java.math.BigInteger
import java.util.LinkedList

class AoC24Day09: Problem(9, 2024, "Disk Fragmenter") {

    data class Block(val size: Int, val fileIndex:Int)

    class Disk(line:String) {

        private var diskSize:Int

        private val diskArray:Array<Int> = Array(10*line.length) { -1 }
        private val diskMap:MutableList<Block> = LinkedList()

        init {
            var fileIndex = 0
            var cursor = 0
            line.forEachIndexed { index: Int, c: Char ->
                val len = c-'0'
                if (index%2==0) { // file
                    repeat(len) {
                        diskArray[cursor] = fileIndex
                        cursor++
                    }
                    diskMap.add(Block(len, fileIndex))
                    fileIndex++
                } else { // empty space
                    cursor += len
                    diskMap.add(Block(len, -1))
                }
            }
            diskSize = cursor
        }

        fun defragment1() {
            var frontCursor = 0
            var backCursor = diskSize-1
            while (frontCursor<backCursor) {
                while (diskArray[frontCursor]>-1 && frontCursor<backCursor) {
                    frontCursor++
                }
                while (diskArray[backCursor]==-1) {
                    backCursor--
                }
                if (frontCursor>=backCursor) {
                    break
                }
                diskArray[frontCursor] = diskArray[backCursor]
                diskArray[backCursor] = -1
                frontCursor++
                backCursor--
            }
            diskSize = backCursor+1
        }

        fun defragment2() {
            var blockToMoveCursor = diskMap.size-1

            while (blockToMoveCursor>0) {
                while (diskMap[blockToMoveCursor].fileIndex==-1 && blockToMoveCursor>0) {
                    blockToMoveCursor--
                }
                if (blockToMoveCursor==0) {
                    break
                }
                val blockToMoveSize = diskMap[blockToMoveCursor].size

                var emptyBlockTempCursor = 0
                while ((diskMap[emptyBlockTempCursor].fileIndex>-1 || diskMap[emptyBlockTempCursor].size<blockToMoveSize)
                    && emptyBlockTempCursor<blockToMoveCursor) {
                    emptyBlockTempCursor++
                }
                if (emptyBlockTempCursor>=blockToMoveCursor) {
                    blockToMoveCursor--
                    continue
                }

                val emptyBlockSize = diskMap[emptyBlockTempCursor].size
                val blockToMove = diskMap[blockToMoveCursor]
                diskMap.removeAt(blockToMoveCursor)
                diskMap.add(blockToMoveCursor, Block(blockToMoveSize, -1))
                diskMap.removeAt(emptyBlockTempCursor)
                diskMap.add(emptyBlockTempCursor, blockToMove)
                diskMap.add(emptyBlockTempCursor+1,Block(emptyBlockSize-blockToMoveSize, -1))
                print("")
            }
        }


        fun checksum1():BigInteger {
            var sum:BigInteger = BigInteger.ZERO
            for (i in 0..<diskSize) {
                sum += BigInteger.valueOf(i*diskArray[i].toLong())
            }
            return sum
        }
        fun checksum2():BigInteger {
            var sum:BigInteger = BigInteger.ZERO
            var cursor = 0
            for (blockIdx in 0..<diskMap.size) {
                if (diskMap[blockIdx].fileIndex>-1) {
                    sum += (0..<diskMap[blockIdx].size).sumOf { pos -> BigInteger.valueOf((cursor+pos)*diskMap[blockIdx].fileIndex.toLong()) }
                }
                cursor += diskMap[blockIdx].size
            }
            return sum
        }
    }

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val disk = Disk(lines[0])
        disk.defragment1()
        return disk.checksum1().toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        val disk = Disk(lines[0])
//        disk.defragment2()
//        return disk.checksum2().toString()
        return "too long.."
    }

}