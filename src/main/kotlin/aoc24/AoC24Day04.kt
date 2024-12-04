package aoc24

import Problem
import kotlin.math.abs

class AoC24Day04: Problem(4, 2024, "Ceres Search") {

    enum class direction(val hor:Int, val ver:Int) {
        N(0,-1),
        NW(1,-1),
        W(1,0),
        SW(1,1),
        S(0,1),
        SE(-1,1),
        E(-1,0),
        NE(-1,-1),
    }

    class Grid(lines: List<String>) {
        val height = lines.size
        val width = lines[0].length
        private val grid = Array(height) { Array(width) { '.' } }

        init {
            for (y in 0..<height) {
                for (x in 0..<width) {
                    grid[y][x] = lines[y][x]
                }
            }
        }

        fun countOccurences(word:String):Int {
            var count = 0
            for (y in 0..<height) {
                for (x in 0..<width) {
                    for (dir in direction.entries) {
                        if (wordMatch(word, x, y, dir)) {
                            count++
                        }
                    }
                }
            }
            return count
        }

        fun countCrosses(word:String):Int {
            var count = 0
            val offset = (word.length - 1)/2
            for (y in 0..<height) {
                for (x in 0..<width) {
                     if ( (wordMatch(word, x, y, direction.SE, offset) || wordMatch(word, x, y, direction.NW, offset))
                         && (wordMatch(word, x, y, direction.SW, offset) || wordMatch(word, x, y, direction.NE, offset)) ) {
                         count++
                     }
                }
            }
            return count
        }

        private fun wordMatch(word:String, x:Int, y:Int, dir:direction, offset:Int=0): Boolean {
            for (i in -offset ..<word.length-offset) {
                val x2 = x + dir.hor * i
                val y2 = y + dir.ver * i
                if (x2 < 0 || x2 >= width || y2 < 0 || y2 >= height) {
                    return false
                }
                if (grid[y2][x2] != word[i+offset]) {
                    return false
                }
            }
            return true
        }
    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val grid = Grid(lines)
        return grid.countOccurences("XMAS").toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        val grid = Grid(lines)
        return grid.countCrosses("MAS").toString()
    }

}