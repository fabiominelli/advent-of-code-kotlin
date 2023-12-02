import AoC23Day02.CubeColor.*
import java.util.*
import kotlin.math.max

class AoC23Day02: DayProblemSolver(2, 2023) {

    enum class CubeColor {BLUE, RED, GREEN}



    private val limit = mapOf(RED to 12, GREEN to 13, BLUE to 14)

    override fun getFirstStarLineOutcome(line: String): Int {

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
            println("$line is possible!")
            game
        } else {
            println("$line is NOT possible!")
            0
        }
    }

    override fun getSecondStarLineOutcome(line: String): Int {

        val limit:MutableMap<CubeColor, Int> = mutableMapOf()

        fun updateMaxWithCube(cube: String) {
            val countColor = cube.trim().split(" ")
            val count = countColor[0].toInt()
            val color = CubeColor.valueOf(countColor[1].uppercase(Locale.getDefault()))
            limit[color] = max(limit[color]?:0, count)
        }

        fun updateMaxWithSet(set: String) {
            set.split(",").forEach { cube -> updateMaxWithCube(cube) }
        }

        val split1 = line.split(":")
        val sets = split1[1].trim().split(";")

        sets.forEach { set -> updateMaxWithSet(set) }
        val power = (limit[RED]?:0)*(limit[GREEN]?:0)*(limit[BLUE]?:0)
        println("$line - power is $power")

        return power
    }


}