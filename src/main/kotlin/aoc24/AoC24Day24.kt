package aoc24

import Problem
import aoc24.AoC24Day24.Operation.*
import kotlin.math.pow

class AoC24Day24: Problem(24, 2024, "Crossed Wires") {

    enum class Operation {
        AND, OR, XOR
    }

    class Gate(val wire1:String, val wire2:String, val op:Operation, val unverifiedWireOut:String) {
        var role:String? = null
        var recognizedWireOut:String? = null
        fun wireOut():String = recognizedWireOut ?: unverifiedWireOut
    }


    class Device(val wires:MutableMap<String,Boolean>, val gates:List<Gate>) {

        val xSize = wires.keys.filter { it.startsWith("x") }.maxOfOrNull { it.drop(1).toInt() }!! + 1
        val ySize = wires.keys.filter { it.startsWith("y") }.maxOfOrNull { it.drop(1).toInt() }!! + 1
        val zSize = gates.map { it.wireOut() }.filter { it.startsWith("z") }.maxOfOrNull { it.drop(1).toInt() }!! + 1

        fun x(i:Int) = "x%02d".format(i)
        fun y(i:Int) = "y%02d".format(i)
        fun z(i:Int) = "z%02d".format(i)

        companion object {
            fun fromLines(lines: List<String>):Device {
                val wires:MutableMap<String,Boolean> = mutableMapOf()
                val sepIndex = lines.indexOf("")
                lines.take(sepIndex).forEach { line ->
                    val (w,v) = line.split(":").let { Pair(it[0], it[1].trim()=="1") }
                    wires[w] = v
                }
                val gates = lines.drop(sepIndex+1).map { line ->
                    val (w1, op, w2, _, wOut) = line.split(" ")
                    val operation = when(op) {
                        "AND" -> Operation.AND
                        "OR" -> Operation.OR
                        "XOR" -> XOR
                        else -> throw IllegalArgumentException("Unknown operation $op")
                    }
                    Gate(w1, w2, operation, wOut)
                }
                return Device(wires, gates)
            }
        }

        val switchedWires = mutableSetOf<String>()

        private fun changeInputTo(xValue:Long, yValue:Long):Device {
            val newWires:MutableMap<String, Boolean> = mutableMapOf()

            // compute digits of values in binary form
            val binary1 = xValue.toString(2).reversed()
            val binary2 = yValue.toString(2).reversed()

            for (i in 0..<xSize) {
                newWires[String.format("x%02d", i)] = if (binary1.length > i) binary1[i] == '1' else false
            }
            for (i in 0..<ySize) {
                newWires[String.format("y%02d", i)] = if (binary2.length > i) binary2[i] == '1' else false
            }

            return Device(newWires, gates)
        }

        var wireOutput:Long = 0L

        fun propagate() {
            val remainingGates = gates.toMutableList()
            while(remainingGates.isNotEmpty()) {
                val gate = remainingGates.firstOrNull { g ->
                    wires[g.wire1] != null && wires[g.wire2] != null }!!
                remainingGates.remove(gate)
                wires[gate.wireOut()] = when(gate.op) {
                    AND -> wires[gate.wire1]!! && wires[gate.wire2]!!
                    OR -> wires[gate.wire1]!! || wires[gate.wire2]!!
                    XOR -> wires[gate.wire1]!! xor wires[gate.wire2]!!
                }
                if (gate.wireOut().startsWith("z") && wires[gate.wireOut()]!!) {
                    val bitValue = 2.0.pow(gate.wireOut().substring(1).toInt()).toLong()
                    wireOutput += bitValue
                }
            }
        }


        fun fixDevice() {
            val recognizedGates = mutableMapOf<String,Gate>()

            (0..<zSize-1).forEach { p ->
                val xorP = gates.find { it.op== XOR && setOf(it.wire1,it.wire2) == setOf(x(p),y(p)) }!!
                xorP.role = "xor$p"
                val andP = gates.find { it.op== AND && setOf(it.wire1,it.wire2) == setOf(x(p),y(p)) }!!
                andP.role = "and$p"
                var digitGate:Gate?
                var carryGate:Gate?
                if (p==0) {
                    digitGate = xorP
                    carryGate = andP
                } else {
                    val prevCarry = gates.find { it.role == "car${p-1}" }!!
                    digitGate = gates.find { it.op == XOR && setOf(it.wire1, it.wire2) == setOf(prevCarry.wireOut(), xorP.wireOut()) }
                    if (digitGate==null) {
                        digitGate = gates.find { it.op == XOR && setOf(it.wire1, it.wire2).any { w -> w==prevCarry.wireOut() || w==xorP.wireOut()} }!!
                        findSwitchedAndFix(prevCarry, xorP, digitGate)
                    }

                    var propCarryGate = gates.find { it.op == AND && setOf(it.wire1, it.wire2) == setOf(prevCarry.wireOut(), xorP.wireOut()) }
                    if (propCarryGate==null) {
                        propCarryGate = gates.find { it.op == AND && setOf(it.wire1, it.wire2).any { w -> w==prevCarry.wireOut() || w==xorP.wireOut()} }!!
                        findSwitchedAndFix(prevCarry, xorP, propCarryGate)
                    }
                    propCarryGate.role = "pca$p"
                    recognizedGates["pca$p"] = propCarryGate

                    carryGate = gates.find { it.op == OR && setOf(it.wire1, it.wire2) == setOf(propCarryGate.wireOut(), andP.wireOut()) }
                    if (carryGate==null) {
                        carryGate = gates.find { it.op == OR && setOf(it.wire1, it.wire2).any { w -> w==propCarryGate.wireOut() || w==andP.wireOut()} }!!
                        findSwitchedAndFix(propCarryGate, andP, carryGate)
                    }
                }
                digitGate.role = "dig$p"
                recognizedGates["dig$p"] = digitGate
                if (digitGate.wireOut() != z(p)) {
                    findOriginGateAndReplace(z(p), digitGate.wireOut())
                    digitGate.recognizedWireOut = z(p)
                }
                carryGate.role = "car$p"
                recognizedGates["car$p"] = carryGate
            }
        }

        private fun findSwitchedAndFix(origin1: Gate, origin2: Gate, targetGate: Gate) {
            when {
                origin1.wireOut()==targetGate.wire1 -> {
                    origin2.recognizedWireOut = targetGate.wire2
                    findOriginGateAndReplace(targetGate.wire2, origin2.unverifiedWireOut)
                }
                origin1.wireOut()==targetGate.wire2 -> {
                    origin2.recognizedWireOut = targetGate.wire1
                    findOriginGateAndReplace(targetGate.wire1, origin2.unverifiedWireOut)
                }
                origin2.wireOut()==targetGate.wire1 -> {
                    origin1.recognizedWireOut = targetGate.wire2
                    findOriginGateAndReplace(targetGate.wire2, origin1.unverifiedWireOut)
                }
                origin2.wireOut()==targetGate.wire2 -> {
                    origin1.recognizedWireOut = targetGate.wire1
                    findOriginGateAndReplace(targetGate.wire1, origin1.unverifiedWireOut)
                }
            }
        }

        private fun findOriginGateAndReplace(output: String, replaceWith: String) {
            switchedWires.add(output)
            switchedWires.add(replaceWith)
            gates.find { it.unverifiedWireOut == output }?.let { it.recognizedWireOut = replaceWith }
        }

    }


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val device = Device.fromLines(lines)
        device.propagate()
        return device.wireOutput.toString()
    }



    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): String {
        val device = Device.fromLines(lines)
        if (isSample) {
            return "n/a"
        } else {
            device.fixDevice()
            return device.switchedWires.sorted().joinToString(",")
        }
    }

}