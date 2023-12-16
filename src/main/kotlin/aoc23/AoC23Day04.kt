package aoc23

import Problem

class AoC23Day04: Problem(4, 2023, "Scratchcards") {

    //=======================
    //     FIRST STAR
    //=======================

    class Card(line:String) {
        private val winning:List<Int>
        private val found:List<Int>
        init {
            val (w,f) = line.split(":")[1].split("|")
            winning = w.trim().split(" +".toRegex()).map { it.toInt() }
            found = f.trim().split(" +".toRegex()).map { it.toInt() }
        }

        fun winCount() = found.count { winning.contains(it) }
    }

    override fun getFirstStarOutcome(lines: List<String>): String {
        return lines.sumOf { line -> Card(line).winCount().let {
            if (it==0) 0 else (1 shl (it-1))
        }}.toString()
    }

    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {
        val cardCount: Array<Int> = Array(lines.size) {1}
        return lines.mapIndexed { row, line ->
            (1..Card(lines[row]).winCount()).forEach { delta -> cardCount[row+delta] += cardCount[row] }
            cardCount[row]
        }.sum().toString()
    }

}