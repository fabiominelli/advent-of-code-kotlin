package aoc24

import Problem
import kotlin.math.abs

class AoC24Day03: Problem(3, 2024, "Mull It Over") {


    class Program(val lines:List<String>) {
        private val mulRegex = Regex("mul\\((\\d+),(\\d+)\\)")
        private val instructionsRegex = Regex("(mul\\((\\d+),(\\d+)\\))|(do\\(\\))|(don't\\(\\))")

        fun sumOfAllMultiplications(): Int {
            return lines.flatMap { mulRegex.findAll(it) }.sumOf { it.groupValues[1].toInt() * it.groupValues[2].toInt() }
        }

        fun sumOfAllEnabledMultiplications(): Int {
            var sum = 0
            var enabled = true
            lines.flatMap { instructionsRegex.findAll(it) }.forEach {
                if (it.groupValues[1].isNotEmpty()) {
                    if (enabled) {
                        sum += it.groupValues[2].toInt() * it.groupValues[3].toInt()
                    }
                } else if (it.groupValues[4].isNotEmpty()) {
                    enabled = true
                } else if (it.groupValues[5].isNotEmpty()) {
                    enabled = false
                }
            }
            return sum
        }
    }

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val program = Program(lines)
        return program.sumOfAllMultiplications().toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        val program = Program(lines)
        return program.sumOfAllEnabledMultiplications().toString()
    }

}