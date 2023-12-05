import java.math.BigInteger

class AoC23Day05: DayProblemSolver(5, 2023) {

    override fun isProblemSolutionBySumOfLines() = false

    private val categories = listOf("soil","fertilizer","water","light","temperature","humidity","location")


    //=======================
    //     FIRST STAR
    //=======================

    override fun getFirstStarOutcome(lines: List<String>): Int {
        val seeds = lines[0].split(":")[1].trim().split(" ").map { it.trim().toBigInteger() }

        // Parsing input
        val maps = parseAllCategoryMaps(lines)

        // Compute
        fun findLocation(seed:BigInteger):BigInteger {
            var value = seed
            categories.forEach { cat ->
                val rangesForCategory = maps[cat]!!
                value = rangesForCategory.firstNotNullOfOrNull { r -> r.mapValue(value) } ?: value
            }
            // last value is location
            return value
        }

        return seeds.minOfOrNull { findLocation(it) }!!.toInt()
    }


    //=======================
    //     SECOND STAR
    //=======================

    override fun getSecondStarOutcome(lines: List<String>): Int {

        // Parsing input
        val sequence = lines[0].split(":")[1].trim().split(" ").map { it.trim().toBigInteger() }
        val sourceRanges = (0..<sequence.size/2).map { idx ->
                                Pair(sequence[2*idx],sequence[2*idx+1])
                            }
        val maps = parseAllCategoryMaps(lines)

        fun findLocationAndDelta(seedRange:Pair<BigInteger,BigInteger>):Pair<BigInteger,BigInteger> {
            var value = seedRange
            categories.forEach { cat ->
                val rangesForCategory = maps[cat]!!
                value = rangesForCategory.firstNotNullOfOrNull { r -> r.mapValueWithDelta(value) } ?: value
            }
            // last value is location
            return value
        }

        var minimumLocation = BigInteger.ZERO

        sourceRanges.forEach { range ->
            // instead of transforming every possible seed (it would take too long) we try to transform
            // "by blocks"
            // The idea is: I start from the first seed in a given range and "send" this seed through
            // the transformation pipeline corresponding to all category mappings; the seed travels along
            // with a delta, representing how many values I am trying to transform together (meaning applying the
            // same shift).
            // At each stage the delta can decrease (never increase) and at the end I get the
            // result location for the seed and a final delta. I know that all the values from seed to seed+delta-1
            // are transformed by the same shift, hence the minimum of those locations is the location of the seed
            // (which is the minimum seed of the range)
            // The following seed to be tested will be the previous seed plus the resulting delta
            var seed = range.first
            while (seed<range.first+range.second) {
                val locationAndDelta = findLocationAndDelta(Pair(seed,range.first+range.second-seed))
                if (minimumLocation==BigInteger.ZERO || locationAndDelta.first<minimumLocation)
                    minimumLocation = locationAndDelta.first
                seed += locationAndDelta.second
            }
        }
        return minimumLocation.toInt()
    }


    //=======================
    //     SHARED
    //=======================

    // Represent an individual range conversion inside a map (e.g. any range in 'seed-to-soil')
    class CategoryRange(line:String) {
        private val destStart:BigInteger
        private val srcStart: BigInteger
        private val len:BigInteger
        init {
            val (d,s,l) = line.split(" ")
            destStart = d.toBigInteger()
            srcStart = s.toBigInteger()
            len = l.toBigInteger()
        }

        // If the provided value is included in the range, provide the transformed value,
        // otherwise null
        fun mapValue(value:BigInteger):BigInteger? {
            return if ( (srcStart..<srcStart+len).contains(value)) {
                destStart + (value-srcStart)
            } else {
                null
            }
        }
        // The provided Pair contains a value to be tested and a delta for which the test may be extended
        // E.g. if the value is 100 and the delta is 50, the caller wants to know:
        // - if the value 100 is transformed by the range
        // - if it is:
        //   - what is the transformed value for 100
        //   - what is the length of the range of values starting from 100 that can be transformed applying the
        //     same addition (so, if 100 became 100+x, all values in the range can be transformed by adding x)
        //     the length *cannot* be larger than the delta passed by the caller
        fun mapValueWithDelta(src:Pair<BigInteger,BigInteger>):Pair<BigInteger,BigInteger>? {
            return if ( (srcStart..<srcStart+len).contains(src.first)) {
                val newValue = destStart + (src.first-srcStart)
                val newDelta = src.second.min(srcStart+len-src.first)
                Pair(newValue, newDelta)
            } else {
                null
            }
        }
    }


    private fun parseAllCategoryMaps(lines: List<String>): MutableMap<String, MutableList<CategoryRange>> {
        val maps: MutableMap<String, MutableList<CategoryRange>> = mutableMapOf()

        categories.forEach { maps[it] = mutableListOf() }
        var currentCategory: String = ""
        lines.drop(1).filter { it.isNotEmpty() }.forEach {
            if (it.contains("map:")) {
                currentCategory = it.split(" ")[0].split("-")[2]
            } else {
                maps[currentCategory]!! += CategoryRange(it)
            }
        }
        return maps
    }


}