package aoc23

import Problem

val partNumbersPattern = "\\b\\d+\\b".toRegex()

class AoC23Day03: Problem(3, 2023, "Gear Ratios") {

    class Matrix(val lines:List<String>) {

        private val height = lines.size
        private val width = lines[0].length

        private var connectedMatrix = Array(height) { BooleanArray(width) }

        private fun setConnected(row:Int, col:Int) {
            if (row in 0..<height && col>=0 && col<width)
                connectedMatrix[row][col] = true
        }

        init {
            lines.forEachIndexed { row, line ->
                line.forEachIndexed {col, ch ->
                    if (!ch.isDigit() && ch!='.') {
                        // above
                        setConnected(row - 1, col - 1)
                        setConnected(row - 1, col)
                        setConnected(row - 1, col + 1)
                        // left and right
                        setConnected(row, col - 1)
                        setConnected(row, col + 1)
                        // below
                        setConnected(row + 1, col - 1)
                        setConnected(row + 1, col)
                        setConnected(row + 1, col + 1)
                    }
                }
            }
        }

        private val neighboursMatrix: Array<Array<MutableList<Int>>> = Array(height) { Array(width) { mutableListOf() } }
        private fun appendPnToNeighbour(row: Int, col: Int, pn:Int) {
            if (row in 0..<height && col >= 0 && col < width)
                neighboursMatrix[row][col] += pn
        }
        init {
            lines.forEachIndexed { row, line ->
                partNumbersPattern.findAll(line).toList().forEach { res ->
                    val value = res.value.toInt()
                    res.range.forEach{ col ->
                        // above
                        appendPnToNeighbour(row-1, col, value)
                        // below
                        appendPnToNeighbour(row+1, col, value)
                    }
                    (-1..1).forEach{ delta ->
                        // left
                        appendPnToNeighbour(row+delta, res.range.first-1, value)
                        // right
                        appendPnToNeighbour(row+delta, res.range.last+1, value)
                    }
                }
            }
        }

        fun sumOfPartNumbers() =
            (0..<height).sumOf { row ->
                partNumbersPattern.findAll(lines[row]).toList().sumOf {res ->
                    if (res.range.any { col -> connectedMatrix[row][col] }) {
                        res.value.toInt()
                    } else {
                        0
                    }
            }
        }

        fun sumOfGearRatios() =
            (0..<height).sumOf { row ->
                lines[row].mapIndexed { col, ch ->
                    if (ch=='*') {
                        neighboursMatrix[row][col].let {
                            if (it.size==2) it[0]*it[1]
                            else 0
                        }
                    } else 0
                }.sum()
            }
    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines:List<String>): String {
        return Matrix(lines).sumOfPartNumbers().toString()
    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {
        return Matrix(lines).sumOfGearRatios().toString()
    }

}