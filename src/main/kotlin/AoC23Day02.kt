import AoC23Day02.CubeColor.*
import java.util.*
import kotlin.math.max

class AoC23Day02: DayProblemSolver(2, 2023) {

    override fun isProblemSolutionBySumOfLines() = true

    enum class CubeColor {BLUE, RED, GREEN}

    //=======================
    //     FIRST STAR
    //=======================

    private val limit = mapOf(RED to 12, GREEN to 13, BLUE to 14)

    override fun getFirstStarLineOutcome(line: String, row: Int): Int {

        fun isCubeDrawPossible(cube: String):Boolean {
            val countColor = cube.trim().split(" ")
            val count = countColor[0].toInt()
            val color = CubeColor.valueOf(countColor[1].uppercase(Locale.getDefault()))
            return count <= (limit[color]?:0)
        }

        fun isSetDrawPossible(set: String):Boolean {
            return set.split(",").all { cube -> isCubeDrawPossible(cube) }
        }

        val split1 = line.split(":")
        val game:Int = split1[0].substring(5).toInt()
        val sets = split1[1].trim().split(";")

        return if (sets.all { set -> isSetDrawPossible(set) }) {
            game
        } else {
            0
        }
    }

    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarLineOutcome(line: String, row: Int): Int {

        val lineLimit: MutableMap<CubeColor, Int> = mutableMapOf()

        fun updateLimitWithCube(cube: String) {
            val countColor = cube.trim().split(" ")
            val count = countColor[0].toInt()
            val color = CubeColor.valueOf(countColor[1].uppercase(Locale.getDefault()))
            lineLimit[color] = max(lineLimit[color] ?: 0, count)
        }

        fun updateLimitsWithSet(set: String) {
            set.split(",").forEach { cube -> updateLimitWithCube(cube) }
        }

        val split1 = line.split(":")
        val sets = split1[1].trim().split(";")

        sets.forEach { set -> updateLimitsWithSet(set) }

        return (lineLimit[RED] ?: 0) * (lineLimit[GREEN] ?: 0) * (lineLimit[BLUE] ?: 0)
    }


}