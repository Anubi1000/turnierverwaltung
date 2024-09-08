package de.anubi1000.turnierverwaltung.util

class Cache<K, V>(maxSize: Int) {
    private val cacheMap: LinkedHashMap<K, V> = object : LinkedHashMap<K, V>(maxSize, 0.75f, true) {
        override fun removeEldestEntry(eldest: Map.Entry<K, V>): Boolean = size > maxSize
    }

    operator fun set(key: K, value: V) {
        cacheMap[key] = value
    }

    operator fun get(key: K): V? = cacheMap[key]

    fun remove(key: K): V? = cacheMap.remove(key)

    fun clear() = cacheMap.clear()

    override fun toString(): String = cacheMap.toString()
}
