package aoc23

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AoC23Day08Test {


    @Test
    fun test1() {
        val network = AoC23Day08.Network(
            """AAA = (BBB, BBB)
               BBB = (AAA, ZZZ)
               ZZZ = (ZZZ, ZZZ)""".trimIndent().lines()
        )
        assertEquals("BBB",network.applyBlock("AAA","L"))
        assertEquals("AAA",network.applyBlock("AAA","LL"))
        assertEquals("BBB",network.applyBlock("AAA","LLR"))
        assertEquals("ZZZ",network.applyBlock("BBB","LLR"))
    }
}