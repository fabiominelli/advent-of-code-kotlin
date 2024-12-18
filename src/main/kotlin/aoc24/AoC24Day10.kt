package aoc24

import Problem
import kotlin.math.abs

class AoC24Day10: Problem(10, 2024, "Hoof It") {


    data class Position(val x: Int, val y: Int) {
        fun neighbours():List<Position> {
            return listOf(
                Position(x,y-1),
                Position(x,y+1),
                Position(x-1,y),
                Position(x+1,y),
            )
        }

        override fun toString(): String {
            return "($x,$y)"
        }
    }

    class Cell(val position:Position, val height:Int) {
        var trails:List<Position> = emptyList()
        init {
            if (height == 9) {
                trails = listOf(position)
            }
        }

        override fun toString(): String {
            return height.toString()
        }
    }

    class Grid(lines: List<String>) {
        private val heightMap:MutableMap<Int,MutableSet<Cell>> = mutableMapOf()

        private val gridWidth = lines[0].length
        private val gridHeight = lines.size
        private val grid:Array<Array<Cell?>> = Array(gridWidth) {
            x -> Array(gridHeight) {
                y -> Cell(Position(x,y),lines[y][x]-'0')
                     .also { cell -> heightMap.computeIfAbsent(cell.height) { mutableSetOf() }.add(cell) }
            }
        }

        private fun isIncluded(position:Position):Boolean {
            return position.x in 0..<gridWidth && position.y in 0..<gridHeight
        }

        private fun at(position:Position):Cell {
            return grid[position.x][position.y]!!
        }

        init {
            (8 downTo 0).forEach { height ->
                // iterate on items in heightMap[h]
                heightMap[height]?.forEach { cell ->
                    cell.position.neighbours()
                        .filter { isIncluded(it) }
                        .map { at(it) }
                        .filter { it.height == height+1 }
                        .forEach { neighbourCell ->
                            cell.trails = cell.trails.plus(neighbourCell.trails)
                        }
                    }
            }
        }

        fun totalScore():Int {
            val cells:Set<Cell> = heightMap[0]?:emptySet()
            return cells.sumOf { it.trails.toSet().size }
        }

        fun totalRatings():Int {
            val cells:Set<Cell> = heightMap[0]?:emptySet()
            return cells.sumOf { it.trails.size }
        }

        fun print() {
            println()
            for (y in 0..<gridHeight) {
                for (x in 0..<gridWidth) {
                    print("${grid[x][y]}(${grid[x][y]!!.trails.size}) ")
                }
                println()
            }
        }
    }

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val grid = Grid(lines)
        return grid.totalScore().toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        val grid = Grid(lines)
        return grid.totalRatings().toString()
    }

}