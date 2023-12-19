package aoc23

import Problem
import aoc23.AoC23Day18.DigPlan.TerrainType.*
import aoc23.AoC23Day18.Direction.*

class AoC23Day18: Problem(18, 2023, "Lavaduct Lagoon") {

    enum class Direction { U, R, D, L }

    data class Position(val row:Int, val col:Int) {
        fun next(direction:Direction, len:Int):Position {
            return when(direction) {
                U -> Position(row-len,col)
                R -> Position(row,col+len)
                D -> Position(row+len,col)
                L -> Position(row,col-len)
            }
        }
    }

    class DigPlan(lines:List<String>, swap:Boolean=false) {

        class DigStep(line:String, swap:Boolean) {
            companion object {
                val regex = "(\\w) (\\d+) \\(#(\\w+)\\)".toRegex()
            }
            val direction:Direction
            val length:Int
            init {
                val (d,l,c) = regex.find(line)!!.groupValues.drop(1)
                if (swap) {
                    direction = Direction.valueOf("RDLU"[c[5].digitToInt()].toString())
                    length = c.substring(0,5).toInt(radix = 16)
                } else {
                    direction = Direction.valueOf(d)
                    length = l.toInt()
                }
            }
        }

        enum class TerrainType(val ch:Char) {
            EMPTY('·'), VERT_DIG('│'), HOR_DIG('─'), DOWN_AND_RIGHT_DIG('┌'), DOWN_AND_LEFT_DIG('┐'), UP_AND_LEFT_DIG('┘'), UP_AND_RIGHT_DIG('└')
        }

        private val steps = lines.map { DigStep(it, swap) }
        private val terrain:Map<Position,TerrainType>

        init {
            var pos = Position(0,0)
            val _terrain:MutableMap<Position,TerrainType> = mutableMapOf()
            steps.zip(steps.drop(1)+steps[0]).forEach { (step, nextStep) ->
                val nextPos = pos.next(step.direction, step.length)
                _terrain[nextPos] =
                    when(Pair(step.direction, nextStep.direction)) {
                        Pair(U,R), Pair(L,D) -> DOWN_AND_RIGHT_DIG
                        Pair(R,D), Pair(U,L) -> DOWN_AND_LEFT_DIG
                        Pair(D,L), Pair(R,U) -> UP_AND_LEFT_DIG
                        Pair(L,U), Pair(D,R) -> UP_AND_RIGHT_DIG
                        else -> throw Exception("Unexpected ste combination")
                    }
                pos = nextPos
            }
            val minRowIndex = _terrain.keys.minOf { it.row }
            val minColIndex = _terrain.keys.minOf { it.col }
            terrain = _terrain.mapKeys { Position(it.key.row-minRowIndex, it.key.col-minColIndex) }
        }
        private val height = terrain.keys.maxOf { it.row } + 1
        private val width = terrain.keys.maxOf { it.col } + 1

        fun lagoonSize():Long {
            var total:Long = 0.toLong()
            var prevSeenRow = -1
            var verticalFromAbove:List<Int> = mutableListOf()

            terrain.entries.groupBy { it.key.row }.toSortedMap().forEach { (row, entryList) ->

                // account for rows without turns
                if (verticalFromAbove.isNotEmpty() && (row-prevSeenRow>1)) {
                    val digsInEachMissingLine = (verticalFromAbove.indices step 2).sumOf { verticalFromAbove[it+1]-verticalFromAbove[it]+1 }.toLong()
                    total += (row-prevSeenRow-1)*digsInEachMissingLine
                }

                val digList:MutableList<Pair<Int,TerrainType>> = entryList.map { entry -> Pair(entry.key.col, entry.value) }.toMutableList()
                verticalFromAbove.forEach { idx -> if (digList.none { it.first == idx }) digList.add(Pair(idx, VERT_DIG))}

                var count = 0.toLong()
                var inside = false
                var lastTurnOrVert = -1
                var nextVerticalFromAbove:MutableList<Int> = mutableListOf()
                for (dig in digList.sortedBy { it.first }) {
                    val col = dig.first
                    val terrainType = dig.second
                    count++
                    when(terrainType) {
                        VERT_DIG -> {
                            if (inside) count += col-lastTurnOrVert-1
                            inside = !inside
                            lastTurnOrVert = col
                            nextVerticalFromAbove.add(col)
                        }
                        DOWN_AND_RIGHT_DIG -> {
                            if (inside) count += (col-lastTurnOrVert-1)
                            inside = !inside
                            nextVerticalFromAbove.add(col)
                            lastTurnOrVert = col
                        }
                        DOWN_AND_LEFT_DIG -> {
                            inside = !inside
                            count += (col-lastTurnOrVert-1)
                            nextVerticalFromAbove.add(col)
                            lastTurnOrVert = col
                        }
                        UP_AND_LEFT_DIG -> {
                            count += (col-lastTurnOrVert-1)
                            lastTurnOrVert = col

                        }
                        UP_AND_RIGHT_DIG -> {
                            if (inside) count += (col-lastTurnOrVert-1)
                            lastTurnOrVert = col
                        }
                        else -> {}
                    }
                }
                total += count
                verticalFromAbove = nextVerticalFromAbove
                prevSeenRow = row
            }
            return total
        }

        fun print() {
            println()
            (0..<height).forEach { r ->
                println()
                (0..<width).forEach {  c ->
                    print((terrain[(Position(r,c))]?:EMPTY).ch)
                }
            }
            println()
            println()
        }
    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val digPlan = DigPlan(lines)
        return digPlan.lagoonSize().toString()
    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {
        val digPlan = DigPlan(lines, true)
        return digPlan.lagoonSize().toString()
    }

}