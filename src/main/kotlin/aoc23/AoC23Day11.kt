package aoc23

import ProblemSolver


class AoC23Day11: ProblemSolver(11, 2023) {

    data class Galaxy(val row:Int, val col:Int) {
        override fun toString(): String {
            return "Galaxy($row, $col)"
        }
    }

    class Universe(val lines:List<String>, val expansion:Int) {
        private val galaxies = expandedGalaxies()
        private val expHeight = galaxies.maxOf { it.row } + 1
        private val expWidth = galaxies.maxOf { it.col } + 1

        private val galaxiesPerRow = galaxies.groupingBy { it.row }.eachCount()
        private val galaxiesPerColumn = galaxies.groupingBy { it.col }.eachCount()

        private fun expandedGalaxies():List<Galaxy> {
            val height = lines.size
            val width = lines.size
            val emptyRows:MutableList<Int> = mutableListOf()
            val emptyCols:MutableList<Int> = (0..<lines[0].length).toMutableList()
            val galaxiesBeforeExpansion = lines.flatMapIndexed() { row, line ->
                line.mapIndexedNotNull() { col, ch ->
                    if (ch=='#') Galaxy(row,col).also { emptyCols.remove(col) } else null
                }.also { if (it.isEmpty()) emptyRows.add(row) }
            }

            val emptyRowsBeforeRow:Array<Int> = Array(height) { 0 }
            emptyRows.forEach { er -> (er+1..<height).forEach { emptyRowsBeforeRow[it]++ } }
            val emptyColumnsBeforeCol:Array<Int> = Array(height) { 0 }
            emptyCols.forEach { ec -> (ec+1..<width).forEach { emptyColumnsBeforeCol[it]++ } }

            return galaxiesBeforeExpansion.map {g ->
                Galaxy(g.row+emptyRowsBeforeRow[g.row]*expansion, g.col+emptyColumnsBeforeCol[g.col]*expansion)
            }
        }

        fun sumOfDistances():Long = sumOfVerticalDistances() + sumOfHorizontalDistances()

        private fun sumOfVerticalDistances():Long {
            var prevNumberOfGalaxiesBelow = galaxies.size
            var prevSumOfVerticalDistancesBelow = 0.toLong()
            val result = (0..<expHeight).sumOf {r ->
                val numberOfGalaxiesBelow = prevNumberOfGalaxiesBelow - (galaxiesPerRow[r]?:0)
                val sumOfVerticalDistancesBelow:Long = if (r==0) galaxies.sumOf { it.row.toLong() } else prevSumOfVerticalDistancesBelow - prevNumberOfGalaxiesBelow
                (galaxiesPerRow[r]?:0)*sumOfVerticalDistancesBelow.also {
                    if (it<0) {
                        println(it)
                    }
                    prevNumberOfGalaxiesBelow = numberOfGalaxiesBelow
                    prevSumOfVerticalDistancesBelow = sumOfVerticalDistancesBelow
                }
            }
            return result
        }
        private fun sumOfHorizontalDistances():Long {
            var prevNumberOfGalaxiesOnTheRight = galaxies.size
            var prevSumOfHorizontalDistancesOnTheRight = 0.toLong()
            val result = (0..<expWidth).sumOf {c ->
                val numberOfGalaxiesOnTheRight = prevNumberOfGalaxiesOnTheRight - (galaxiesPerColumn[c]?:0)
                val sumOfHorizontalDistancesOnTheRight:Long = if (c==0) galaxies.sumOf { it.col.toLong() } else prevSumOfHorizontalDistancesOnTheRight - prevNumberOfGalaxiesOnTheRight
                (galaxiesPerColumn[c]?:0)*sumOfHorizontalDistancesOnTheRight.also {
                    prevNumberOfGalaxiesOnTheRight = numberOfGalaxiesOnTheRight
                    prevSumOfHorizontalDistancesOnTheRight = sumOfHorizontalDistancesOnTheRight
                }
            }
            return result
        }

    }

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        return Universe(lines,1).sumOfDistances().toString()
    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {
        return Universe(lines,999999).sumOfDistances().toString()
    }

}