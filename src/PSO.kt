import java.util.*

interface Particle {
    val personalBest: DoubleArray
    val personalBestFitness: Int
    val velocity: DoubleArray
    val position: DoubleArray
    val currentFitness: Int
    val size: Int
        get() = velocity.size
    fun move()
    fun recalculateFitness()
}

fun <T: Particle> applyPSO(particles: Array<T>, rand: Random, iterations: Int, m: Double, c1: Double, c2: Double): T {
    require(particles.isNotEmpty())

    var best: T = particles.minBy { it.personalBestFitness } ?: particles[0]
    for(i in 0 until iterations) {
        for(p in particles) {
            for(j in 0 until p.size) {
                var vel = p.velocity[j]
                vel *= m
                vel += c1 * rand.nextDouble() * (p.personalBest[j] - p.position[j])
                vel += c2 * rand.nextDouble() * (best.personalBest[j] - p.position[j])
                p.velocity[j] = vel
            }
            p.move()
        }
        best = particles.minBy { it.personalBestFitness } ?: best
    }
    return best
}