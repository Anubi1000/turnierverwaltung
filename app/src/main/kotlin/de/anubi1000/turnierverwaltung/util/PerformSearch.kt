package de.anubi1000.turnierverwaltung.util

inline fun <T> List<T>.performSearch(query: String, itemTransform: (T) -> String): List<T> {
    if (query.isEmpty()) return this

    val searchParts = query.lowercase().split(' ')

    return filter { item ->
        var search = itemTransform(item).lowercase()
        searchParts.all { part ->
            if (search.contains(part)) {
                search = search.replaceFirst(part, " ")
                true
            } else {
                false
            }
        }
    }
}
