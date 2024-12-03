// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

import kotlin.time.Duration
import kotlin.time.measureTime


fun <T> timeResult(block: () -> T): Pair<Duration, T> {
    var result: T
    val time = measureTime { result = block() }
    return Pair(time, result)
}
