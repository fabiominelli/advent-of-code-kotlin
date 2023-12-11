package aoc23

import Problem

class AoC23Day03: Problem(3, 2023) {

    override fun isProblemSolutionBySumOfLines() = true

    private var rows = 0
    private var cols = 0



    //=======================
    //     FIRST STAR
    //=======================

    private var connectedMatrix: Array<BooleanArray> = Array(0) { BooleanArray(0) }

    override fun firstStarPreprocessInput(lines:List<String>) {

        fun setConnected(row:Int, col:Int) {
            if (row in 0..<rows && col>=0 && col<cols)
                connectedMatrix[row][col] = true
        }

        rows = lines.size
        cols = lines[0].length
        connectedMatrix = Array(rows) { BooleanArray(cols) }

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

    private val partNumbersPattern = "\\b\\d+\\b".toRegex()

    override fun getFirstStarLineOutcome(line: String, row: Int): Int {
        return partNumbersPattern.findAll(line).toList().sumOf {res ->
            if (res.range.any { col -> connectedMatrix[row][col] }) {
                res.value.toInt()
            } else {
                0
            }
        }
    }



    //=======================
    //     SECOND STAR
    //=======================

    private var neighboursMatrix: Array<Array<MutableList<Int>>> = emptyArray()

    override fun secondStarPreprocessInput(lines:List<String>) {

        fun appendPartNumber(row: Int, col: Int, pn:Int) {
            if (row in 0..<rows && col >= 0 && col < cols)
                neighboursMatrix[row][col] += pn
        }

        rows = lines.size
        cols = lines[0].length
        neighboursMatrix = Array(rows) { Array(cols) { mutableListOf() } }

        lines.forEachIndexed { row, line ->
            partNumbersPattern.findAll(line).toList().forEach { res ->
                val value = res.value.toInt()
                res.range.forEach{ col ->
                    // above
                    appendPartNumber(row-1, col, value)
                    // below
                    appendPartNumber(row+1, col, value)
                }
                (-1..1).forEach{ delta ->
                    // left
                    appendPartNumber(row+delta, res.range.first-1, value)
                    // right
                    appendPartNumber(row+delta, res.range.last+1, value)
                }
            }
        }
    }

    override fun getSecondStarLineOutcome(line: String, row: Int): Int {
        return line.mapIndexed { col, ch ->
            if (ch=='*') {
                neighboursMatrix[row][col].let {
                    if (it.size==2) it[0]*it[1]
                    else 0
                }
            } else 0
        }.sum()
    }

}