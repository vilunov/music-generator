import java.lang.Math.*
import java.util.*

class ParticleChords(rand: Random, val scale: Scale, val segment: Segment): Particle {
    override val size: Int
        get() = segment.rhythm.chordDurations.size
    override var personalBestFitness: Int = Int.MAX_VALUE
    override val position: DoubleArray = DoubleArray(size, { rand.nextDouble() * 7 - 0.5 })
    override val velocity: DoubleArray = DoubleArray(size, { 0.0 })
    override var currentFitness: Int = 0
    override val personalBest: DoubleArray
        get() = personalBestBaked.map(Int::toDouble).toDoubleArray()
    var personalBestBaked: IntArray = IntArray(size, { 0 })
        private set

    init {
        position[0] = segment.startingNote.toDouble()
        recalculateFitness()
    }

    override fun recalculateFitness() {
        val baked = position.map(::round).map(Long::toInt).toIntArray()

        val subsequences = run {
            var cur = baked[0]
            var total = 0
            var len = 0
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

        val repetitions = run {
            val counts = HashMap<Int, Int>()
            baked.forEach({ counts.put(it, counts[it]?.plus(1) ?: 0) })
            counts.map { (_, j) -> j * j }.fold(0, { a, b -> a + b })
        }

        currentFitness = subsequences * 10 + repetitions
        if (currentFitness < personalBestFitness) {
            personalBestBaked = baked
            personalBestFitness = currentFitness
        }
    }

    override fun move() {
        for(i in 1 until size) position[i] += velocity[i]
        recalculateFitness()
    }
}

fun generateChords(rand: Random, scale: Scale, segment: Segment): List<Chord> {
    val swarmSize = 10
    val particle = applyPSO(
            Array(swarmSize, { ParticleChords(rand, scale, segment) }),
            rand, 20, 0.9, 0.7, 0.7)
    return List(particle.size, { Chord(
            listOf(particle.personalBestBaked[it],
                    particle.personalBestBaked[it] + 2,
                    particle.personalBestBaked[it] + 4),
            scale) })
}