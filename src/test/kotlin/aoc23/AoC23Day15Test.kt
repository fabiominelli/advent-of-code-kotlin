package aoc23


import aoc23.AoC23Day15.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AoC23Day15Test {

    @Test
    fun testHash() {
        assertEquals(200, hash("H"))
        assertEquals(52, hash("HASH"))
        assertEquals(30, hash("rn=1"))
        assertEquals(0, hash("rn"))
    }

    @Test
    fun testStep() {
        assertEquals(RemoveStep("aaa"), Step.from("aaa-") )
        assertEquals(AddStep("aaa",5), Step.from("aaa=5") )
    }

}