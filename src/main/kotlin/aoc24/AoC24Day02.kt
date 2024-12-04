package aoc24

import Problem
import kotlin.math.abs

class AoC24Day02: Problem(2, 2024, "Red-Nosed Reports") {

    class Report(private val values:List<Int>) {
        private val differences = values.zip(values.drop(1)).map { it.second - it.first }
        private fun isMonotonic():Boolean {
            return differences.all { it > 0 } || differences.all { it < 0 }
        }
        private fun noSuddenChanges():Boolean {
            return differences.all { abs(it)<4 }
        }
        fun isSafe(dampening:Boolean=false):Boolean {
            if (isMonotonic() && noSuddenChanges()) {
                return true
            } else if (dampening) {
                val reduced = values.indices.map { Report(values.subList(0, it).plus(values.subList(it+1, values.size))) }
                return isSafe() || reduced.any {it.isSafe(false)}
            } else {
                return false
            }
        }
    }


    class Reports(lines:List<String>) {
        private val reportList:List<Report> = lines.map { line -> Report(line.split(" ").map { it.toInt() }) }
        fun countSafe(dampened:Boolean=false):Int {
//            println()
//            println (reportList.mapIndexed() { index, report -> "Report ${index+1}: ${if (report.isSafe()) "SAFE" else "UNSAFE"}" }.joinToString("\n"))
            return reportList.count { it.isSafe(dampened) }
        }
    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        return Reports(lines).countSafe().toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        return Reports(lines).countSafe(true).toString()
    }

}