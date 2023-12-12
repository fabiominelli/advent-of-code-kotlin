package aoc23

import aoc23.AoC23Day12.Row
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AoC23Day12Test {

    private val p = AoC23Day12()

    @Test
    fun test() {
        assertEquals(0, p.countArrangements("#.#", listOf(1)))
        assertEquals(1, p.countArrangements("#", listOf(1)))
        assertEquals(0, p.countArrangements("#", listOf(2)))
        assertEquals(1, p.countArrangements("#.#", listOf(1,1)))
        assertEquals(1, p.countArrangements("??#", listOf(2)))
        assertEquals(2, p.countArrangements("??", listOf(1)))
        assertEquals(1, p.countArrangements("??.", listOf(2)))
        assertEquals(4, p.countArrangements("??.??", listOf(1,1)))
        assertEquals(8, p.countArrangements("??.??.???", listOf(1,1,2)))
        assertEquals(3, p.countArrangements("?????", listOf(3)))
        assertEquals(1, p.countArrangements("?????", listOf(1, 1, 1)))
        assertEquals(1, p.countArrangements("???.###", listOf(1, 1, 3)))
        assertEquals(1, p.countArrangements("?#??#??", listOf(3, 1)))
        assertEquals(10, p.countArrangements("?###????????", listOf(3, 2, 1)))
        assertEquals(16, p.countArrangements("?#??#???.??.???", listOf(3, 1, 1, 1, 1)))
        assertEquals(29, p.countArrangements("?.?????????#?", listOf(1,1,1)))
    }

    @Test
    fun test2() {
        assertEquals(1, p.countArrangements("???.### 1,1,3",1))
        assertEquals(1, p.countArrangements("???.### 1,1,3",5))
    }

}