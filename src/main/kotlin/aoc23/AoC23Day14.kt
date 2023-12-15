package aoc23

import Problem


class AoC23Day14: Problem(14, 2023, "Parabolic Reflector Dish") {

    data class Platform(val lines:List<String>) {

        companion object {
            fun rollRowLeft(row:String):String {
                val newRow = row.map { if (it=='#') '#' else '.'}.toMutableList()
                row.indices.filter { col-> row[col]=='O' }.forEach { idx ->
                    var firstObstacle = idx-1
                    while (firstObstacle>=0 && newRow[firstObstacle]=='.') firstObstacle--
                    newRow[firstObstacle+1] = 'O'
                }
                return String(newRow.toCharArray())
            }

        }
        private val height = lines.size
        private val width = lines[0].length

        private fun transposed() = Platform(
            (0..<width).map { col ->
                (0..<height).map { row ->
                    lines[row][col]
                }.joinToString("")
            }
        )

        fun rollWest():Platform = Platform(lines.map { rollRowLeft(it) })
        fun rollEast():Platform = Platform(lines.map { rollRowLeft(it.reversed()).reversed() })
        fun rollNorth():Platform = transposed().rollWest().transposed()
        fun rollSouth():Platform = transposed().rollEast().transposed()

        fun rollCycle():Platform = rollNorth().rollWest().rollSouth().rollEast()

        fun rollCycleTimes(times:Int):Platform {
            var platform = this
            val platform2Count:MutableMap<Platform,Int> = mutableMapOf(platform to 0)
            val platformList = mutableListOf(platform)
            var count = 0
            var cycleStart = -1
            var cycleLength = -1
            while (true) {
                platform = platform.rollCycle()
                count++
                val seenAt = platform2Count[platform]
                if (seenAt!=null) {
                    cycleStart = seenAt
                    cycleLength = count-seenAt
                    break
                } else {
                    platform2Count[platform] = count
                    platformList.add(platform)
                }
            }
            return platformList[cycleStart + (times - cycleStart) % cycleLength]
        }

        fun load() = lines.mapIndexed { r, line -> (height-r)*line.count { it=='O' } }.sum()

    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        return Platform(lines).rollNorth().load().toString()
    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {
        return Platform(lines).rollCycleTimes(1_000_000_000).load().toString()
    }

}