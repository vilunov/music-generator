object Settings {
    val output_file: String = "a_sharp_dorian_long.mid"
    val scale_root: Int = 70
    val scale_type: ScaleType = ScaleType.Dorian
    val segments: Int = 8

    //PSO #1
    object Structure {
        val swarm_size: Int = 10
        val iterations: Int = 20
        val m: Double = 0.4
        val c1: Double = 1.5
        val c2: Double = 1.5
    }

    //PSO #2
    object Chord {
        val swarm_size: Int = 10
        val iterations: Int = 20
        val m: Double = 0.4
        val c1: Double = 1.5
        val c2: Double = 1.5
    }

    //PSO #3
    object Melody {
        val swarm_size: Int = 50
        val iterations: Int = 100
        val m: Double = 0.8
        val c1: Double = 1.2
        val c2: Double = 1.2
    }
}