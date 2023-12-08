package aoc23

import ProblemSolver


class AoC23Day08: ProblemSolver(8, 2023) {

    class Network(lines:List<String>) {

        class Arc(val left: String, val right: String)

        val arcs = getArcsMap(lines)

        private fun getArcsMap(lines:List<String>):Map<String, Arc> {
            val pattern = "(\\w+) = \\((\\w+), (\\w+)\\)".toRegex()
            return lines.associate {
                val (source, left, right) = pattern.find(it.trim())!!.groupValues.drop(1)
                source to Arc(left, right)
            }
        }

        fun applyBlock(source:String, block:String):String {
            return block.fold(source) {
               acc, instruction -> if (instruction=='L') arcs[acc]!!.left else arcs[acc]!!.right
            }
        }
    }

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val instructionBlock = lines[0]
        val network = Network(lines.drop(2))

        var position = "AAA"
        var count = 0
        while (position!="ZZZ") {
            position = network.applyBlock(position, instructionBlock)
            count += instructionBlock.length
        }
        return count.toString()
    }


    //=======================
    //     SECOND STAR
    //=======================

    private fun blockRepetitionsToFinish(start:String, network:Network, instructionBlock:String):Int {
        var count = 0
        var pos = start
        while(!pos.endsWith('Z')) {
            count++
            pos = network.applyBlock(pos, instructionBlock)
        }
        return count
    }

    override fun getSecondStarOutcome(lines: List<String>): String {
        // I hold the following assumption, not stated in the problem but which is verified in the inputs:
        // - for each starting node xxA, there is a minimum number N of repetitions of the instruction block that lead to
        //   a finishing node xxZ (without landing, at the end of any block iteration, on another yyZ finishing node)
        // - applying the instruction block one more time, from the xxZ node, the destination is the same
        //   of when applying once the block from xxA
        // - so, there is a cycle: from xxA, repeating the instruction block K*N times (with K any positive integer)
        //   will always lead to xxZ
        // - also, there is no other way to land to a finishing node from xxA

        // The conclusion is that I have to find, for each staring node, the number of repetitions that lead
        // to its associated finishing node.
        // The solution is the least common multiple of all this number (multiplied by the length of the instruction
        // block)

        val instructionBlock = lines[0]
        val network = Network(lines.drop(2))

        val startPositions = network.arcs.keys.filter { it.endsWith('A') }.toList()

        val repetitions = startPositions.map { blockRepetitionsToFinish(it, network, instructionBlock).toLong() }
        return (lcm(repetitions)*instructionBlock.length).toString()
    }


    private fun gcd(first: Long, second: Long): Long {
        var a = first
        var b = second
        while (b > 0) {
            val temp = b
            b = a % b
            a = temp
        }
        return a
    }

    private fun lcm(a: Long, b: Long):Long = a * (b / gcd(a, b))

    private fun lcm(list:List<Long>):Long = list.reduce { acc, next -> lcm(acc,next) }
}