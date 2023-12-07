fun main() {

    val problems = mapOf(
        1 to AoC23Day01(),
        2 to AoC23Day02(),
        3 to AoC23Day03(),
        4 to AoC23Day04(),
        5 to AoC23Day05(),
        6 to AoC23Day06(),
        7 to AoC23Day07(),
    )

    println()
    println ("=========================================================")
    println ("|     ||       Part 1          ||       Part 2          |")
    println ("|     ||-----------------------||-----------------------|")
    println ("| Day ||  Sample   | My Input  ||  Sample   | My Input  |")
    println ("|=====||===========|===========||===========|===========|")

    problems.forEach { (day, problem) ->

        println ("| %3d || %9d | %9d || %9d | %9d |".format(
            day,
            problem.solveForExample(star = 1),
            problem.solveForMyInput(star = 1),
            problem.solveForExample(star = 2),
            problem.solveForMyInput(star = 2)
        ))
    }

    println ("=========================================================")
    println()

}