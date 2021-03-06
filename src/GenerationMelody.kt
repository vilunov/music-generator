import java.lang.Math.*
import java.util.*

class ParticleMelody(rand: Random, val scale: Scale, val segment: Segment, val chords: List<Chord>): Particle {
    override val size: Int
        get() = segment.rhythm.noteDurations.size
    lateinit var personalBestBaked: IntArray
    override val personalBest: DoubleArray
        get() = personalBestBaked.map(Int::toDouble).toDoubleArray()
    lateinit var melody: IntArray
        private set
    override var personalBestFitness: Int = Int.MAX_VALUE
    override val velocity: DoubleArray = DoubleArray(size, {0.0})
    override val position: DoubleArray = DoubleArray(size, {
        val i = segment.rhythm.correspondingChord(it)
        if (i != null) { rand.nextDouble() * 6 - 0.5 }
        else { rand.nextDouble() * 14 - 0.5 }
    })
    override var currentFitness: Int = 0

    init {
        position[0] = 0.0
        recalculateFitness()
    }

    fun step(value: Int, idx: Int): Int {
        val i = segment.rhythm.correspondingChord(idx)
        return if (i != null) chords[i][value]
        else value
    }

    override fun recalculateFitness() {
        val baked = position.map(::round).map(Long::toInt).toIntArray()
        val bakedSteps = baked.mapIndexed { idx, i -> step(i, idx) }.toIntArray()

        val distance = run {
            fun dst(a: Int, b: Int): Int = (abs(a - b) - 1) * (abs(a - b) - 1)
            (1 until size).map { dst(bakedSteps[it], bakedSteps[it - 1]) }.sum() + dst(bakedSteps[size - 1], segment.endingNote)
        }

        currentFitness = distance

        if (currentFitness < personalBestFitness) {
            personalBestBaked = baked
            melody = bakedSteps
            personalBestFitness = currentFitness
        }
    }

    override fun move() {
        for(i in 1 until size) position[i] += velocity[i]
        recalculateFitness()
    }
}

fun generateMelody(rand: Random, scale: Scale, segment: Segment, chords: List<Chord>): List<Int> {
    val swarmSize = Settings.Melody.swarm_size
    val particle = applyPSO(
            Array(swarmSize, { ParticleMelody(rand, scale, segment, chords) }),
            rand, Settings.Melody.iterations, Settings.Melody.m, Settings.Melody.c1, Settings.Melody.c2)
    return List(particle.size, { particle.melody[it] })
}