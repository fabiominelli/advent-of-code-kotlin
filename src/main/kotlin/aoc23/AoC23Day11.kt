package aoc23

import Problem
import java.util.*


class AoC23Day11: Problem(11, 2023, "Cosmic Expansion") {

    data class Galaxy(val row:Int, val col:Int) {
        override fun toString(): String {
            return "Galaxy($row, $col)"
        }
    }

    class Universe(val lines:List<String>, private val expansion:Int) {

        private val galaxies:List<Galaxy>

        init {
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

            galaxies = galaxiesBeforeExpansion.map {g ->
                Galaxy(g.row+emptyRowsBeforeRow[g.row]*expansion, g.col+emptyColumnsBeforeCol[g.col]*expansion)
            }
        }

        private val galaxiesPerRow = galaxies.groupByTo(TreeMap()) { it.row }
        private val galaxiesPerColumn = galaxies.groupByTo(TreeMap()) { it.col }


        fun sumOfDistances():Long = sumOfProjectedDistances(true) + sumOfProjectedDistances(false)

        private fun sumOfProjectedDistances(vertical:Boolean):Long {
            var prevLayer = -1
            var prevNumberOfFollowingGalaxies = galaxies.size
            var prevSumOfProjectedDistancesToFollowing = galaxies.sumOf { 1 + if (vertical) it.row.toLong() else it.col.toLong() }
            var res = 0.toLong()
            val galaxyGrouping = if (vertical) galaxiesPerRow else galaxiesPerColumn
            // iterating over non-empty rows (or columns)
            galaxyGrouping.forEach { layer, galaxyList ->
                val galaxyInLayerCount = galaxyList.size
                val numberOfFollowingGalaxies = prevNumberOfFollowingGalaxies - galaxyInLayerCount
                // Calculating the sum of all vertical (or horizontal) distances between a galaxy on this row (or column) and
                // all the galaxies below; the sum is obtained by taking the sum computed for previous non-empty row (or column)
                // and subtracting the number of galaxies in this row and below, multiplied by the rows (or cols) difference
                // This works because, for example, moving from row1 to row2, all galaxy below are closer by row2-row1
                val sumOfProjctedDistancesToFollowing:Long = prevSumOfProjectedDistancesToFollowing - prevNumberOfFollowingGalaxies*(layer-prevLayer)
                // The grand total is increased by the sum computed above, multiplied by the number of galaxies in the
                // current row or column (because the sum above is the sum of distances from single galaxy in this layer
                // to all the following)
                res += galaxyInLayerCount*sumOfProjctedDistancesToFollowing

                prevLayer = layer
                prevNumberOfFollowingGalaxies = numberOfFollowingGalaxies
                prevSumOfProjectedDistancesToFollowing = sumOfProjctedDistancesToFollowing
            }
            return res
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