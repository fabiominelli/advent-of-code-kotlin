package aoc23

import DayProblemSolver
import kotlin.math.floor
import kotlin.math.sqrt

class AoC23Day06: DayProblemSolver(6, 2023) {

    override fun isProblemSolutionBySumOfLines() = false

    class Race(private val time:Long, private val record:Long) {
        fun winningWaysCount(): Int {
            val root = (time - sqrt((time * time - 4 * record).toDouble())) / 2
            return (time + 1 - (floor(root).toInt() + 1) * 2).toInt()
        }
    }

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {

        val timeList = lines[0].split(':')[1].trim().split(" +".toRegex()).map { it.toLong() }
        val recordList = lines[1].split(':')[1].trim().split(" +".toRegex()).map { it.toLong() }
        val races = timeList.zip(recordList) { t, r -> Race(t,r) }

        return races.map { it.winningWaysCount() }.foldRight(1) { ways, acc -> ways*acc}.toString()

    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {
        val time = lines[0].split(':')[1].trim().split(" +".toRegex()).joinToString("").toLong()
        val record = lines[1].split(':')[1].trim().split(" +".toRegex()).joinToString("").toLong()
        return Race(time, record).winningWaysCount().toString()
    }

}