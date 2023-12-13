package aoc23

import aoc23.AoC23Day13.Companion.isReflectedAroundPos
import aoc23.AoC23Day13.Pattern
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class AoC23Day13Test {


    @Test
    fun testVerticalReflection() {
        assert(isReflectedAroundPos("##",1))
        assert(isReflectedAroundPos("###",1))
        assert(isReflectedAroundPos("###",2))
        assert(isReflectedAroundPos(".##",2))

        assert(isReflectedAroundPos("###..###",1))
        assertFalse(isReflectedAroundPos("###..###",2))
        assertFalse(isReflectedAroundPos("###..###",3))
        assert(isReflectedAroundPos("###..###",4))
    }

    @Test
    fun testHorizontalReflection() {
        assertEquals(1, Pattern(listOf("##","##")).horizontalReflection())
        assertEquals(2, Pattern(listOf("..","##","##")).horizontalReflection())
    }

    @Test
    fun test() {
        assertEquals(3,Pattern(listOf(".####.#","##..###")).summarize())
        assertEquals(1,Pattern(listOf("##")).summarize())
    }


}