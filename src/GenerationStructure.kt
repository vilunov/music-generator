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

        val transitions = (1 until baked.size)
                .filter { baked[it] != baked[it - 1] }.count()

        val counts: HashMap<Int, Int> = HashMap()
        baked.forEach { counts[it] = counts[it]?.plus(1) ?: 1}

        currentFitness = transitions * 5 + counts.map { (_, j) -> j * j }.fold(0, {a, b -> a + b})

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
    val swarmSize = 10
    val particle = applyPSO(Array(swarmSize, { ParticleStructure(segments, rand) }), rand, 100, 0.9, 0.7, 0.7)
    val notes = MutableList(swarmSize + 1, { rand.nextInt(7) })
    notes[swarmSize] = 0
    return List(segments, {
        Segment(
            Patterns.patterns[particle.personalBestBaked[it]],
            notes[it], notes[it + 1])
    })
}