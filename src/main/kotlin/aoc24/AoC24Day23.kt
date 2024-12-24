package aoc24

import Problem

class AoC24Day23: Problem(23, 2024, "LAN Party") {

    data class Connection(val first: String, val second: String)

    class Network(lines: List<String>) {
        private val computers = mutableSetOf<String>()

        private val connections = mutableMapOf<String, MutableList<String>>()
        init {
            lines.forEach { line ->
                val (first,second) = line.split("-")
                computers.add(first)
                computers.add(second)
                connections.getOrPut(first) { mutableListOf() }.add(second)
                connections.getOrPut(second) { mutableListOf() }.add(first)
            }
        }


        fun get3Loops(): Set<Set<String>> {
            val loops = mutableSetOf<Set<String>>()
            val remaining = computers.filter { it.startsWith("t") }.toSortedSet()
            while (remaining.isNotEmpty()) {
                val computer = remaining.first()
                remaining.remove(computer)
                connections[computer]!!.forEach { first ->
                    connections[first]!!.forEach { second ->
                        if (second != computer && connections[second]!!.contains(computer)) {
                            loops.add(setOf(computer, first, second))
                        }
                    }
                }
            }
            return loops
        }

        fun findPassword():String {

            // Exploration showed that:
            // - all nodes are connected to exactly other 13 nodes (4 in the sample)
            // - there is no LAN Party of 14 (5 in the sample)
            // - must look for LAN Party of 13 (4 in the sample)
            // We cycle through all nodes, until we find a node (center) fr which all its neighbours but one
            // are completely connected to each other.
            computers.forEach { center ->
                val neighbours = connections[center]!!
                val included = mutableSetOf<String>() // nodes that are connected to all other neighbours but one
                neighbours.forEach { first ->
                    val firstNeighbours = connections[first]!!
                    val missing = neighbours.minus(first).filter { !firstNeighbours.contains(it) }
                    if (missing.size==1) {
                        included.add(first)
                    }
                }
                if (included.size==neighbours.size-1) {
                    // all neighbours but one, are connected to all neighbours but one
                    // this is teh LAN Party
                    val password = included.plus(center).sorted().joinToString(",")
                    return password
                }
            }
            return "Not found"
        }

    }

    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): String {
        val network = Network(lines)
        val loops = network.get3Loops()
        return loops.size.toString()
    }



    //=======================
    //     SECOND STAR
    //=======================


    override fun getSecondStarOutcome(lines: List<String>): String {
        val network = Network(lines)
        return network.findPassword()
    }

}