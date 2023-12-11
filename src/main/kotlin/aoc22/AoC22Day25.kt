package aoc22

import Problem

class AoC22Day25: Problem(25, 2022) {

    override fun isProblemSolutionBySumOfLines() = false

    class Snafu(val value:String) {

        private val digitMap = mapOf('2' to 2, '1' to 1, '0' to 0, '-' to -1, '=' to -2)

        val decimalValue = toDecimal()

        companion object {
            fun fromDecimal(d:Long):Snafu {
                var residual = d
                var result = ""
                while(residual>0) {
                    val wouldBeDigit = residual.mod(5)
                    var carry:Int
                    if (wouldBeDigit<3) {
                        result = wouldBeDigit.toString() + result
                        carry = 0
                    } else {
                        val digit = if (wouldBeDigit==3) "=" else "-"
                        result = digit+result
                        carry = 1
                    }
                    residual = (residual - wouldBeDigit)/5 + carry
                }
                return Snafu(result.ifBlank { "0" })
            }
        }

        private fun toDecimal():Long {
            var acc:Long = 0
            value.apply {
                var pow = 1.toLong()
                indices.forEach { exp ->
                    acc += pow*digitMap[get(length-exp-1)]!!
                    pow *= 5
                }
            }
            return acc
        }

    }



    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        return Snafu.fromDecimal(lines.sumOf { Snafu(it).decimalValue }).value
    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {
        return "n/a"
    }

}