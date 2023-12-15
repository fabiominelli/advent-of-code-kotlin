package aoc23


import aoc23.AoC23Day14.Platform
import aoc23.AoC23Day14.Platform.Companion.rollRowLeft
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AoC23Day14Test {

    @Test
    fun testRollLeft() {
        assertEquals("O", rollRowLeft("O"))
        assertEquals("#O", rollRowLeft("#O"))
        assertEquals("O.", rollRowLeft(".O"))
        assertEquals("O...", rollRowLeft("...O"))
        assertEquals(".#O", rollRowLeft(".#O"))
        assertEquals("..#O.", rollRowLeft("..#.O"))
        assertEquals("..#OO..#OO..", rollRowLeft("..#.O.O#O..O"))
    }

    @Test
    fun testRollCycle() {
        val platform = Platform(listOf("..", ".O"))
        assertEquals(Platform(listOf(".O", "..")), platform.rollNorth())
        assertEquals(Platform(listOf("O.", "..")), platform.rollNorth().rollWest())
        assertEquals(Platform(listOf("..", "O.")), platform.rollNorth().rollWest().rollSouth())
        assertEquals(Platform(listOf("..", ".O")), platform.rollNorth().rollWest().rollSouth().rollEast())
        assertEquals(Platform(listOf("..", ".O")), platform.rollCycle())
    }
}