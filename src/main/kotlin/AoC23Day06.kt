import kotlin.math.floor
import kotlin.math.sqrt

class AoC23Day06: DayProblemSolver(6, 2023) {

    override fun isProblemSolutionBySumOfLines() = false


    //=======================
    //     FIRST STAR
    //=======================

    class Race(val time:Long, val record:Long)

    override fun getFirstStarOutcome(lines: List<String>): Int {

        val timeList = lines[0].split(':')[1].trim().split(" +".toRegex()).map { it.toLong() }
        val recordList = lines[1].split(':')[1].trim().split(" +".toRegex()).map { it.toLong() }
        val races = timeList.zip(recordList) { t, r -> Race(t,r) }

        return races.map { winningWaysCount(it) }.foldRight(1) { ways, acc -> ways*acc}

    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): Int {
        val time = lines[0].split(':')[1].trim().split(" +".toRegex()).joinToString("").toLong()
        val record = lines[1].split(':')[1].trim().split(" +".toRegex()).joinToString("").toLong()
        return winningWaysCount(Race(time, record))
    }


    //=======================
    //     SHARED
    //=======================

    private fun winningWaysCount(race:Race):Int {
        val root = (race.time - sqrt((race.time*race.time-4*race.record).toDouble()))/2
        return (race.time + 1 - (floor(root).toInt()+1)*2).toInt()
    }

}