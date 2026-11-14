package com.pokedaisy.app.companion.data

import java.util.concurrent.ConcurrentHashMap

/** ROM-owned names for the hash-verified Unbound 2.1.1.1 Chinese build only. */
object UnboundChineseBattleNames {
    @Volatile private var enabled = false
    private val speciesCache = ConcurrentHashMap<Int, String>()
    private val moveCache = ConcurrentHashMap<Int, String>()
    fun use(enabled: Boolean) {
        this.enabled = enabled
        speciesCache.clear()
        moveCache.clear()
    }
    private fun read(id: Int, range: IntRange, base: Long, stride: Int, cache: ConcurrentHashMap<Int, String>): String? {
        if (!enabled || id !in range) return null
        cache[id]?.let { return it }
        val value = runCatching {
            val raw = PokedexSource.reader.readCoreMemory(base + id.toLong() * stride, stride)
            if (raw.none { (it.toInt() and 0xff) == 0xff }) return@runCatching null
            Gen3Text.decode(raw, charmap = CjkTables.ELITE_REDUX_CN)
                .trim().takeIf { it.isNotEmpty() && Gen3Text.UNKNOWN !in it }
        }.getOrNull() ?: return null
        cache[id] = value
        return value
    }
    fun fallback(original: String): String = if (enabled) untranslatedGameText(original) else original
    /** Chinese text decoded from this identified ROM is already translated. */
    fun dexOriginal(original: String): String =
        if (enabled && Gen3Text.UNKNOWN !in original && original.any { it in '\u3400'..'\u9fff' }) original
        else untranslatedGameText(original)
    fun cachedSpecies(id: Int): String? = if (enabled) speciesCache[id] else null
    fun cachedMove(id: Int): String? = if (enabled) moveCache[id] else null
    fun species(id: Int): String? = read(id, 1..1293, 0x0966A98CL, 11, speciesCache)
    fun move(id: Int): String? = read(id, 1..922, 0x08A40A10L, 13, moveCache)
}
