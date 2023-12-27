package aoc23

import Problem
import java.util.PriorityQueue
import java.util.Queue

class AoC23Day21: Problem(21, 2023, "Step Counter") {

    data class Position(val row:Int, val col:Int) {

        fun neighbours() = setOf(
            Position(row-1,col),
            Position(row+1, col),
            Position(row,col-1),
            Position(row,col+1),
        )
        override fun toString(): String {
            return "($row,$col)"
        }
    }

    sealed class Cell(val position:Position) {
        override fun toString(): String {
            return "${this.javaClass.simpleName}($position)"
        }
    }
    class Plot(position:Position):Cell(position)
    class Rock(position:Position):Cell(position)
    class Garden(val gardenMap:Map<Position,Cell>, val start:Position) {
        companion object {
            fun fromMap(lines:List<String>):Garden {
                var start:Position? = null
                val map = buildMap<Position,Cell> {
                    lines.forEachIndexed { row, line ->
                        line.forEachIndexed { col, c ->
                            val p=Position(row,col)
                            put(p,if (c=='#') Rock(p) else Plot(p))
                            if (c=='S') start=p
                        }
                    }
                }
                return Garden(map, start!!)
            }
        }

        val height:Int = gardenMap.keys.maxOf { it.row } + 1
        val width:Int = gardenMap.keys.maxOf { it.col } + 1

        private fun neighboursWithPlot(pos:Position):Set<Position> =
            pos.neighbours().filter { gardenMap.containsKey(it) &&  gardenMap[it] is Plot}.toSet()

        private fun distancesFrom(source:Position):Map<Position,Int> {
            val distances:MutableMap<Position,Int> = mutableMapOf()
            val toExplore:Queue<Pair<Position,Int>> = PriorityQueue(compareBy { it.second })
            distances[source] = 0
            toExplore.add(Pair(source,0))
            do {
                val (pos, distance) = toExplore.remove()
                neighboursWithPlot(pos).filter { !distances.containsKey(it) }.forEach {
                    distances[it] = distance+1
                    toExplore.add(Pair(it, distance+1))
                }
            } while(toExplore.isNotEmpty())
            return distances
        }

        private val distancesFromStart = distancesFrom(start)

        fun countReachablePlotsFromStart(parity:Int) = distancesFromStart.entries.count { it.value%2==parity }

        fun countReachableWithin(maxSteps:Int, source:Position):Int {
            val distances = if (source==start) distancesFromStart else distancesFrom(source)
            return distances.values.count { it%2==maxSteps%2 && it<=maxSteps }
        }
    }

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val garden = Garden.fromMap(lines)
        return garden.countReachableWithin(if (lines.size<15) 6 else 64, garden.start).toString()
    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {

        if (lines.size<20) return "n/a"

        val garden = Garden.fromMap(lines)

//        garden.printDistancesFrom(garden.start, garden.height*2)

        val sectorSize = garden.height  // 131
        assert(garden.width==sectorSize)

        val maxSteps = if (lines.size<20) 50 else 26501365
        val coveredAreaRadius = (maxSteps/sectorSize).toLong()  // 202300, nr of sectors completely covered going straight up/down/left/right
                                                    // of the starting one (excluding the starting one)
        assert ( maxSteps % sectorSize == (sectorSize-1)/2)

        // Among the reachable sectors: in half of them the reachable plots are the same of the original one;
        // in the other half, parity of cells is inverted, and a different number of plots is reachable
        val countOfCoveredSectorsOriginalParity = (coveredAreaRadius-1)*(coveredAreaRadius-1)
        val countOfCoveredSectorsInvertedParity = coveredAreaRadius*coveredAreaRadius

        val totalReachablePlots =
            countOfCoveredSectorsOriginalParity * garden.countReachablePlotsFromStart(maxSteps%2) +
            countOfCoveredSectorsInvertedParity * garden.countReachablePlotsFromStart((maxSteps+1)%2) +

            garden.countReachableWithin(sectorSize-1,Position(0,(sectorSize-1)/2)) +
            garden.countReachableWithin(sectorSize-1,Position(sectorSize-1,(sectorSize-1)/2)) +
            garden.countReachableWithin(sectorSize-1,Position((sectorSize-1)/2,0)) +
            garden.countReachableWithin(sectorSize-1,Position((sectorSize-1)/2,sectorSize-1)) +

            coveredAreaRadius * (
                    garden.countReachableWithin((sectorSize-3)/2,Position(sectorSize-1,0)) +
                    garden.countReachableWithin((sectorSize-3)/2,Position(sectorSize-1,sectorSize-1)) +
                    garden.countReachableWithin((sectorSize-3)/2,Position(0,0)) +
                    garden.countReachableWithin((sectorSize-3)/2,Position(0,sectorSize-1))
                    ) +

            (coveredAreaRadius-1) * (
                    garden.countReachableWithin((sectorSize-1)*3/2,Position(sectorSize-1,0)) +
                    garden.countReachableWithin((sectorSize-1)*3/2,Position(sectorSize-1,sectorSize-1)) +
                    garden.countReachableWithin((sectorSize-1)*3/2,Position(0,0)) +
                    garden.countReachableWithin((sectorSize-1)*3/2,Position(0,sectorSize-1))
                    )

        return totalReachablePlots.toString()
    }

}