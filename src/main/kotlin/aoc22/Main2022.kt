package aoc22

fun main() {

    val problems = mapOf(
        24 to AoC22Day24(),
        25 to AoC22Day25(),
    )

    println()
    println ("===============================================================================")
    println ("|     ||          Part 1                  ||          Part 2                  |")
    println ("|     ||----------------------------------||----------------------------------|")
    println ("| Day ||  Sample   | My Input             ||  Sample   | My Input             |")
    println ("|=====||===========|======================||===========|======================|")

    problems.forEach { (day, problem) ->

        println ("| %3s || %9s | %20s || %9s | %20s |".format(
            day,
            problem.solveForExample(star = 1),
            problem.solveForMyInput(star = 1),
            problem.solveForExample(star = 2),
            problem.solveForMyInput(star = 2)
        ))
    }

    println ("===============================================================================")
    println()

}