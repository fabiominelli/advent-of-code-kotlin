package aoc24

import Problem
import kotlin.math.pow

class AoC24Day17: Problem(17, 2024, "Chronospatial Computer") {

    class Register(line:String) {
        val name = line[9]
        val value = line.substring(12).toLong()
    }

    enum class Operation(val code:Int) {
        ADV(0),
        BXL(1),
        BST(2),
        JNZ(3),
        BXC(4),
        OUT(5),
        BDV(6),
        CDV(7);
        companion object {
            fun fromCode(code:Int) = entries.first { it.code == code }
        }
    }

    data class Instruction(val operation: Operation, val operand: Int)

    class Program(line:String) {
        var iPointer = 0
        val items = line.substring(9).split(",").map { it.toInt() }
        fun getNextInstruction(): Instruction? {
            if (iPointer >= items.size-1) return null
            val instruction = Instruction(Operation.fromCode(items[iPointer]), items[iPointer+1])
            iPointer += 2
            return instruction
        }
    }

    class Memory(lines: List<String>) {
        private val emptyLineIndex = lines.indexOf("")
        val registries = lines.subList(0, emptyLineIndex)
            .map { Register(it) }
            .associateBy { it.name }
        val program = Program(lines[lines.size-1])
        var regA = registries['A']!!.value
        var regB = registries['B']!!.value
        var regC = registries['C']!!.value

        val output: MutableList<Int> = mutableListOf()

        fun executeProgram() {
            while(true) {
                val instruction = program.getNextInstruction() ?: break
                executeInstruction(instruction)
            }
        }

        private fun executeInstruction(instruction: Instruction) {
            val operandComboValue:Long = when(instruction.operand) {
                in 0..3 -> instruction.operand.toLong()
                4 -> regA
                5 -> regB
                6 -> regC
                else -> throw Exception("Invalid operand value")
            }
            when(instruction.operation) {
                Operation.ADV -> {
                    regA /= 2.0.pow(operandComboValue.toInt()).toInt()
                }
                Operation.BXL -> {
                    regB = regB xor instruction.operand.toLong()
                }
                Operation.BST -> {
                    regB = operandComboValue % 8
                }
                Operation.JNZ -> {
                    if (regA>0) {
                        program.iPointer = instruction.operand
                    }
                }
                Operation.BXC -> {
                    regB = regB xor regC
                }
                Operation.OUT -> {
                    output.add((operandComboValue % 8).toInt())
                }
                Operation.BDV -> {
                    regB = regA / 2.0.pow(operandComboValue.toInt()).toInt()
                }
                Operation.CDV -> {
                    regC = regA / 2.0.pow(operandComboValue.toInt()).toInt()
                }
            }
        }
    }

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val memory = Memory(lines)
        memory.executeProgram()
        return memory.output.joinToString( ",")
    }



    //=======================
    //     SECOND STAR
    //=======================


    private fun solve(target:List<Int>, lines:List<String>): List<Long> {

        if (target.isEmpty()) return listOf(0)
        val subSolutions = solve(target.drop(1), lines)

        return subSolutions.flatMap { base ->
            val found = (0..7)
                .map { base*8+it }
                .filter { c ->
                    val memory = Memory(lines)
                    memory.regA = c
                    memory.executeProgram()
                    memory.output.first()==target.first()
                }
            found
        }
    }

    override fun getSecondStarOutcome(lines: List<String>): String {
        val memory = Memory(lines)
        val target = memory.program.items
        return solve(target, lines).min().toString()
    }

}