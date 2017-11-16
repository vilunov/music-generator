import java.util.*

fun main(args: Array<String>) {
    val random = Random()
    val scale = Scale(60, ScaleType.Major)
    val rhythm = Rhythm(intArrayOf(2, 2, 2, 2, 2, 2, 2, 2).toList(),
            intArrayOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1).toList())
    val chords = generateChords(random, rhythm)
    val melody = generateMelody(random, rhythm, chords, scale)
    println(chords[0].root)
}

fun generateChords(rand: Random, rhythm: Rhythm) : List<Chord> {
    val PARTICLES_NUM = 10
    val ITERATIONS = 10
    val M = 0.9; val C1 = 0.7; val C2 = 0.7

    val particles = Array(PARTICLES_NUM, {_ -> ParticleChords(rand, rhythm) })

    var best: ParticleChords = particles.minBy { i -> i.fitness } ?: particles[0]
    for(i in 0 until ITERATIONS) {
        for(p in particles) {
            for(j in 0 until p.length) {
                var vel = p.velocity[j]
                vel *= M
                vel += C1 * rand.nextDouble() * (p.personalBest[j].toDouble() - p.chords[j])
                vel += C2 * rand.nextDouble() * (best.personalBest[j].toDouble() - p.chords[j])
                p.velocity[j] = vel
            }
            p.move()
        }
        best = particles.minBy { i -> i.fitness } ?: best
    }

    return best.toSequence()
}

fun generateMelody(rand: Random, rhythm: Rhythm, chords: List<Chord>, scale: Scale) : List<Int> {
    val PARTICLES_NUM = 30
    val ITERATIONS = 100
    val M = 0.9; val C1 = 0.7; val C2 = 0.7

    val particles = Array(PARTICLES_NUM, {_ -> ParticleNotes(rand, rhythm, chords) })

    var best: ParticleNotes = particles.minBy { i -> i.fitness } ?: particles[0]
    for(i in 0 until ITERATIONS) {
        for(p in particles) {
            for(j in 0 until p.length) {
                var vel = p.velocity[j]
                vel *= M
                vel += C1 * rand.nextDouble() * (p.personalBest[j].toDouble() - p.notes[j])
                vel += C2 * rand.nextDouble() * (best.personalBest[j].toDouble() - p.notes[j])
                p.velocity[j] = vel
            }
            p.move()
        }
        best = particles.minBy { i -> i.fitness } ?: best
    }

    return best.toSequence(scale)
}