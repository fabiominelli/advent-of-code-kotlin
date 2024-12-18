package aoc24

import Problem
import java.math.BigInteger

class AoC24Day13: Problem(13, 2024, "Claw Contraption") {

    class Arcade(lines:List<String>) {
        private val machines = mutableListOf<Machine>()
        init {
            splitLines(lines).forEach {
                machines.add(Machine(it))
            }
        }

        fun cheapestToPossiblePrizes(boost:Boolean):BigInteger {
            return machines
                .mapNotNull { it.cheapestToPrize(boost) }
                .reduce(BigInteger::add)
        }
    }

    data class Button(val dx:Int, val dy:Int)
    data class Position(val x:Int, val y:Int)

    class Machine(lines:List<String>) {

        private val buttonA: Button
        private val buttonB: Button
        private val prize: Position

        init {
            val btnPattern = """Button (A|B): X\+(-?\d+), Y\+(-?\d+)""".toRegex()
            var tempButtonA: Button? = null
            var tempButtonB: Button? = null
            (0..1).forEach { idx ->
                val match = btnPattern.matchEntire(lines[idx]) ?: throw Exception("Invalid input")
                val (name, x, y) = match.destructured
                if (name == "A") {
                    tempButtonA = Button(x.toInt(), y.toInt())
                } else {
                    tempButtonB = Button(x.toInt(), y.toInt())
                }
            }
            buttonA = tempButtonA ?: throw Exception("Button A not found")
            buttonB = tempButtonB ?: throw Exception("Button B not found")

            val prizePattern = """Prize: X=(-?\d+), Y=(-?\d+)""".toRegex()
            val match = prizePattern.matchEntire(lines[2]) ?: throw Exception("Invalid input")
            val (x, y) = match.destructured
            prize = Position(x.toInt(), y.toInt())
        }


        fun cheapestToPrize(boost: Boolean): BigInteger? {
            val x0 = (if (boost) 10000000000000 else 0L).toBigInteger()+prize.x.toBigInteger()
            val y0 = (if (boost) 10000000000000 else 0L).toBigInteger()+prize.y.toBigInteger()

            val countA_num = buttonB.dy.toBigInteger()*x0 - buttonB.dx.toBigInteger()*y0
            val countA_den = buttonA.dx.toBigInteger()*buttonB.dy.toBigInteger() - buttonA.dy.toBigInteger()*buttonB.dx.toBigInteger()

            if (countA_num % countA_den != BigInteger.ZERO) {
                return null
            }
            val countA = countA_num / countA_den
            if (countA < BigInteger.ZERO) {
                return null
            }
            val countB = (x0 - countA*buttonA.dx.toBigInteger()) / buttonB.dx.toBigInteger()
            if (countB < BigInteger.ZERO) {
                return null
            }
            return (countA*3.toBigInteger()+countB)
        }


    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val arcade = Arcade(lines)
        return arcade.cheapestToPossiblePrizes(false).toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        val arcade = Arcade(lines)
        return arcade.cheapestToPossiblePrizes(true).toString()
    }

}