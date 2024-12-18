package aoc24

import Problem
import kotlin.math.abs

class AoC24Day08: Problem(8, 2024, "Resonant Collinearity") {

    data class Position(val x: Int, val y: Int) {
    }

    class Grid(lines: List<String>) {
        val width = lines[0].length
        val height = lines.size
        private val cells:Array<Array<Char?>> = Array(width) { Array(height) { null } }
        private val antennas:MutableMap<Char,MutableList<Position>> = mutableMapOf()

        init {
            for (y in 0..<height) {
                for (x in 0..<width) {
                    val frequency = lines[y][x]
                    if (frequency != '.') {
                        cells[x][y] = frequency
                        antennas.computeIfAbsent(frequency) { mutableListOf() }.add(Position(x, y))
                    }
                }
            }

        }

        fun antinodes1(): Set<Position> {
            val antinodes:MutableSet<Position> = mutableSetOf()
            for (antennaType in antennas) {
                val positions = antennaType.value
                for (first in 0..<positions.size) {
                    for (second in first+1..<positions.size) {
                        val pos1 = positions[first]
                        val pos2 = positions[second]
                        val diff = Pair(pos2.x - pos1.x, pos2.y - pos1.y)
                        val newAntinodes = listOf(
                            Position(pos1.x - diff.first, pos1.y - diff.second),
                            Position(pos2.x + diff.first, pos2.y + diff.second)
                        )
                        antinodes.addAll(newAntinodes.filter { it.x in 0..<width && it.y in 0 ..<height })
                    }
                }
            }
            return antinodes
        }

        fun gcd(a: Int, b: Int): Int {
            return if (b == 0) a else gcd(b, a % b)
        }

        fun antinodes2(): Set<Position> {
            val antinodes:MutableSet<Position> = mutableSetOf()
            for (antennaType in antennas) {
                val positions = antennaType.value
                for (first in 0..<positions.size) {
                    for (second in first+1..<positions.size) {
                        antinodes.addAll(allAntinodes(positions[first], positions[second]))
                        antinodes.addAll(allAntinodes(positions[second], positions[first]))
                    }
                }
            }
            return antinodes
        }

        private fun allAntinodes(pos1:Position, pos2:Position):List<Position> {
            val antinodes = mutableListOf<Position>()
            val diffGcd = abs(gcd(pos2.x - pos1.x, pos2.y - pos1.y))
            val diff = Pair((pos2.x - pos1.x)/diffGcd, (pos2.y - pos1.y)/diffGcd)
            var current = pos1
            while (current.x in 0..<width && current.y in 0..<height) {
                antinodes.add(current)
                current = Position(current.x + diff.first, current.y + diff.second)
            }
            return antinodes
        }

    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val grid = Grid(lines)
        return grid.antinodes1().size.toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        val grid = Grid(lines)
        return grid.antinodes2().size.toString()
    }

}