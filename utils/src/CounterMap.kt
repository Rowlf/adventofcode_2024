// (C) 2024 A.Voß, a.voss@fh-aachen.de, kotlin@codebasedlearning.dev

// Kotlins delegation is really cool :-) (inspired by Pythons Counter from collections)
class CounterMap<T>(
    private val delegate: MutableMap<T, Long> = mutableMapOf()
) : MutableMap<T,Long> by delegate {
    constructor(vararg iter:T):this() { addAll(iter.asIterable()) }
    constructor(iter: Iterable<T>):this() { addAll(iter) }
    constructor(map: CounterMap<T>):this() { addAll(map) }

    override operator fun get(key:T):Long =delegate.getOrDefault(key,0L)

    fun addAll(list: Iterable<T>) = apply { list.forEach { this[it] += 1 } }
    fun addAll(map: CounterMap<T>) = apply { map.forEach { this[it.key] += it.value  } }

    override fun toString():String = delegate.toString()
}
