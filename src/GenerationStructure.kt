import java.lang.Math.round
import java.util.*

object Patterns {
    private val dotted = Rhythm(
            intArrayOf(4, 4, 4, 4).toList(),
            intArrayOf(3, 1, 3, 1, 3, 1, 3, 1).toList())
    private val standard = Rhythm(
            intArrayOf(4, 4, 4, 4).toList(),
            intArrayOf(2, 2, 2, 2, 2, 2, 2, 2).toList())
    private val standard2 = Rhythm(
            intArrayOf(8, 8).toList(),
            intArrayOf(2, 2, 2, 2, 2, 2, 2, 2).toList())
    val patterns = listOf(dotted, standard, standard2)
}

class ParticleStructure(length: Int, rand: Random): Particle {
    override var personalBestFitness: Int = Int.MAX_VALUE
    override var currentFitness: Int = 0
    override val position: DoubleArray = DoubleArray(length, { rand.nextDouble() * Patterns.patterns.size - 0.5 })
    override val velocity: DoubleArray = DoubleArray(length, { 0.0 })
    lateinit var personalBestBaked: IntArray
    override val personalBest: DoubleArray
        get() = personalBestBaked.map(Int::toDouble).toDoubleArray()

    init {
        recalculateFitness()
    }

    override fun recalculateFitness() {
        val baked = position.map(::round).map(Long::toInt).toIntArray()

        val subsequences = run {
            var cur = baked[0]
            var total = 0
            var len = -3
            for (i in 1 until size) {
                if (baked[i] == cur) {
                    len++
                } else {
                    total += len * len
                    len = 0
                    cur = baked[i]
                }
            }
            total + len * len
        }
        val counts: HashMap<Int, Int> = HashMap()
        baked.forEach { counts[it] = counts[it]?.plus(1) ?: 1}

        currentFitness = subsequences * 5 + counts.map { (_, j) -> j * j }.fold(0, {a, b -> a + b})

        if (currentFitness < personalBestFitness) {
            personalBestBaked = baked
            personalBestFitness = currentFitness
        }
    }

    override fun move() {
        for(j in 0 until size) {
            position[j] += velocity[j]
            position[j] = minOf(position[j], Patterns.patterns.size.toDouble() - 0.501)
            position[j] = maxOf(position[j], -0.5)
        }
        recalculateFitness()
    }
}

fun generateStructure(segments: Int, rand: Random): List<Segment> {
    val swarmSize = Settings.Structure.swarm_size
    val particle = applyPSO(Array(swarmSize, { ParticleStructure(segments, rand) }), rand,
            Settings.Structure.iterations, Settings.Structure.m, Settings.Structure.c1, Settings.Structure.c2)
    val notes = MutableList(swarmSize + 1, { rand.nextInt(7) })
    notes[swarmSize] = 0
    return List(segments, {
        Segment(
            Patterns.patterns[particle.personalBestBaked[it]],
            notes[it], notes[it + 1])
    })
}