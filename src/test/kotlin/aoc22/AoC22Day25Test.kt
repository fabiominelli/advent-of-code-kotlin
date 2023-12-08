package aoc22

import aoc22.AoC22Day25.Snafu
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource


class AoC22Day25Test {

    @ParameterizedTest(name = "{0} => {1}")
    @CsvSource(
        "0,0",
        "1,1",
        "10,5",
        "12111, 906",
        "2=0=, 198",
        "21, 11",
        "2=01, 201",
        "111, 31",
        "20012, 1257",
        "112, 32",
        "1=-1=, 353",
        "1-12, 107",
        "12, 7",
        "1=, 3",
        "122, 37",
    )
    fun `is Snafu to Decimal Correct`(snafu:String, decimal:Long) {
        assertEquals(decimal, Snafu(snafu).decimalValue)
    }

    @ParameterizedTest(name = "{0} => {1}")
    @CsvSource(
        "0,0",
        "1,1",
        "10,5",
        "12111, 906",
        "2=0=, 198",
        "21, 11",
        "2=01, 201",
        "111, 31",
        "20012, 1257",
        "112, 32",
        "1=-1=, 353",
        "1-12, 107",
        "12, 7",
        "1=, 3",
        "122, 37",
    )
    fun `is Snafu from Decimal Correct`(snafu:String, decimal:Long) {
        assertEquals(snafu, Snafu.fromDecimal(decimal).value)
    }


}