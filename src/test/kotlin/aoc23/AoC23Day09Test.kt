package aoc23

import aoc23.AoC23Day09.History
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AoC23Day09Test {


    @Test
    fun testDiffs() {
        assertEquals(listOf(3, 3, 3, 3, 3), History("0 3 6 9 12 15").diffs().values)
        assertEquals(listOf(0, 0, 0, 0), History("3 3 3 3 3").diffs().values)
    }

    @Test
    fun testNextValue() {
        val history = History("0 3 6 9 12 15")
        assertEquals(18, history.nextValue())
    }

    @Test
    fun testPreviousValue() {
        val history = History("0 3 6 9 12 15")
        assertEquals(-3, history.previousValue())
    }

}