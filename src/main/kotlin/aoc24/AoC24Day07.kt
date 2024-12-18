package aoc24

import Problem
import java.math.BigInteger

class AoC24Day07: Problem(7, 2024, "Bridge Repair") {

    enum class Operator {
        ADD, MULTIPLY, CONCATENATE;

        fun apply(a: BigInteger, b: BigInteger): BigInteger {
            return when(this) {
                ADD -> a.plus(b)
                MULTIPLY -> a.times(b)
                CONCATENATE -> "$a$b".toBigInteger()
            }
        }
    }

    data class Equation(val result:BigInteger, val operands:List<BigInteger>) {
        fun canBeTrue(useConcatenate:Boolean): Boolean {
            if (operands.size==1) return operands[0]==result
            if (operands[0]>result) return false
            return Operator.entries.any { op ->
                if (!useConcatenate && op==Operator.CONCATENATE) return false
                Equation(result, listOf(op.apply(operands[0], operands[1])) + operands.drop(2)).canBeTrue(useConcatenate)
            }
        }
    }

    private fun equationsFrom(lines: List<String>): List<Equation> {
        return lines.map { line ->
            val parts = line.split(": ")
            val result = parts[0].toBigInteger()
            val operands = parts[1].split(" ").map { it.toBigInteger() }
            Equation(result, operands)
        }
    }

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        return equationsFrom(lines).filter { it.canBeTrue(false) }.sumOf { it.result }.toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        return equationsFrom(lines).filter { it.canBeTrue(true) }.sumOf { it.result }.toString()
    }

}