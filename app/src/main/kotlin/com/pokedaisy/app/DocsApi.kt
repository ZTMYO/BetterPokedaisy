package com.pokedaisy.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

/**
 * The user manual, as the website serves it: docs/manual's Markdown turned into blocks by website/src/lib/docs.js
 * and published at [URL] with each release of the site (so it matches the app people can download). The
 * same blocks are the website's /docs/ pages, so the Markdown is the one place the manual is written.
 *
 * The app keeps the last copy it fetched (and every image it showed) under filesDir/docs, for reading
 * offline; [DocsStore.refresh] asks again with the copy's ETag, so an unchanged manual costs one small reply.
 */
object DocsApi {
    const val URL = "https://pokedaisy.com/api/docs/v1/docs.json"
    const val ZH_URL = "https://pokedaisy.com/api/docs/v1/zh-hans/docs.json"
    /** The API version this reader draws; a newer one gets its own path (/v2/) and older apps keep /v1/. */
    const val VERSION = 1

    class Manual(
        val rev: String,
        val site: String,
        val sections: List<Section>,
        val pages: List<Page>,
        val synonyms: Map<String, List<String>>,
    ) {
        private val bySlug = pages.associateBy { it.slug }
        fun page(slug: String): Page? = bySlug[slug]
        fun next(slug: String): Page? = pages.getOrNull(pages.indexOfFirst { it.slug == slug } + 1)?.takeIf { slug in bySlug }
        fun previous(slug: String): Page? = pages.indexOfFirst { it.slug == slug }.takeIf { it > 0 }?.let { pages[it - 1] }
        fun section(id: String): Section? = sections.firstOrNull { it.id == id }
    }

    class Section(val id: String, val title: String, val pages: List<String>)

    class Page(
        val slug: String,
        val title: String,
        val summary: String,
        val section: String,
        val keywords: List<String>,
        val blocks: List<Block>,
    )

    /** A run of text with its marks; [page] / [anchor] link inside the manual, [href] outside it. */
    data class Span(
        val text: String,
        val bold: Boolean = false,
        val italic: Boolean = false,
        val code: Boolean = false,
        val page: String? = null,
        val anchor: String? = null,
        val href: String? = null,
    )

    sealed class Block {
        data class Heading(val level: Int, val id: String, val text: String) : Block()
        data class Paragraph(val spans: List<Span>) : Block()
        data class Bullets(val ordered: Boolean, val start: Int, val items: List<List<Block>>) : Block()
        /** [version]: a hash of the picture, so a changed screenshot is fetched again and an unchanged one never is. */
        data class Image(val src: String, val alt: String, val caption: String?, val width: Int, val height: Int, val version: String) : Block()
        data class Note(val kind: String, val blocks: List<Block>) : Block()
        data class Table(val head: List<List<Span>>, val rows: List<List<List<Span>>>) : Block()
        object Rule : Block()
    }

    /** [json] as the API serves it. Throws on a reply this reader can't use (another version, not JSON). */
    fun parse(json: String): Manual {
        val o = JSONObject(json)
        require(o.optInt("version") == VERSION) { "docs API version ${o.optInt("version")}" }
        val synonyms = o.optJSONObject("synonyms")?.let { s ->
            s.keys().asSequence().associateWith { k -> strings(s.optJSONArray(k)) }
        }.orEmpty()
        return Manual(
            rev = o.optString("rev"),
            site = o.optString("site", "https://pokedaisy.com").trimEnd('/'),
            sections = objects(o.optJSONArray("sections")).map { Section(it.getString("id"), it.getString("title"), strings(it.optJSONArray("pages"))) },
            pages = objects(o.optJSONArray("pages")).map { p ->
                Page(
                    slug = p.getString("slug"),
                    title = p.getString("title"),
                    summary = p.optString("summary"),
                    section = p.optString("section"),
                    keywords = strings(p.optJSONArray("keywords")),
                    blocks = blocks(p.optJSONArray("blocks")),
                )
            },
            synonyms = synonyms,
        )
    }

    private fun objects(a: JSONArray?): List<JSONObject> = if (a == null) emptyList() else (0 until a.length()).map { a.getJSONObject(it) }
    private fun strings(a: JSONArray?): List<String> = if (a == null) emptyList() else (0 until a.length()).map { a.getString(it) }

    private fun spans(a: JSONArray?): List<Span> = objects(a).map { s ->
        Span(
            text = s.optString("text"),
            bold = s.optBoolean("b"),
            italic = s.optBoolean("i"),
            code = s.optBoolean("code"),
            page = s.optString("page").ifEmpty { null },
            anchor = s.optString("anchor").ifEmpty { null },
            href = s.optString("href").ifEmpty { null },
        )
    }

    /** Blocks this reader doesn't know (a newer site) are skipped rather than failing the whole page. */
    private fun blocks(a: JSONArray?): List<Block> = objects(a).mapNotNull { b ->
        when (b.optString("t")) {
            "h2", "h3" -> Block.Heading(b.getString("t").drop(1).toInt(), b.optString("id"), b.optString("text"))
            "p" -> Block.Paragraph(spans(b.optJSONArray("c")))
            "ul", "ol" -> b.optJSONArray("items").let { items ->
                Block.Bullets(
                    ordered = b.getString("t") == "ol",
                    start = b.optInt("start", 1),
                    items = if (items == null) emptyList() else (0 until items.length()).map { blocks(items.getJSONArray(it)) },
                )
            }
            "img" -> Block.Image(b.optString("src"), b.optString("alt"), b.optString("caption").ifEmpty { null }, b.optInt("w"), b.optInt("h"), b.optString("v"))
            "note" -> Block.Note(b.optString("kind", "note"), blocks(b.optJSONArray("c")))
            "table" -> Block.Table(
                head = b.optJSONArray("head").let { h -> if (h == null) emptyList() else (0 until h.length()).map { spans(h.getJSONArray(it)) } },
                rows = b.optJSONArray("rows").let { r ->
                    if (r == null) emptyList() else (0 until r.length()).map { i ->
                        val row = r.getJSONArray(i)
                        (0 until row.length()).map { spans(row.getJSONArray(it)) }
                    }
                },
            )
            "hr" -> Block.Rule
            else -> null
        }
    }
}

/** Fetches, keeps and serves the manual and its images (filesDir/docs). Blocking calls: run them off the UI thread. */
object DocsStore {
    private fun dir(context: Context) = File(context.filesDir, "docs").apply { mkdirs() }
    private fun manualFile(context: Context, chinese: Boolean) = File(dir(context), if (chinese) "docs-zh-hans.json" else "docs.json")
    private fun etagFile(context: Context, chinese: Boolean) = File(dir(context), if (chinese) "docs-zh-hans.etag" else "docs.etag")

    private fun bundledChinese(context: Context): DocsApi.Manual? = runCatching {
        context.assets.open("docs-zh-hans.json").bufferedReader(Charsets.UTF_8).use { DocsApi.parse(it.readText()) }
    }.getOrNull()

    private fun chineseBaseFile(context: Context) = File(dir(context), "docs-zh-hans.base")

    /** A copy downloaded for this bundled version, or the bundled Chinese manual on first use. */
    fun cached(context: Context, chinese: Boolean = false): DocsApi.Manual? {
        val bundled = if (chinese) bundledChinese(context) else null
        if (chinese && bundled != null && chineseBaseFile(context).takeIf { it.isFile }?.readText() != bundled.rev) return bundled
        return runCatching { DocsApi.parse(manualFile(context, chinese).readText()) }.getOrNull() ?: bundled
    }

    sealed class Refresh {
        class Updated(val manual: DocsApi.Manual) : Refresh()
        object Unchanged : Refresh()
        class Failed(val offline: Boolean) : Refresh()
    }

    /** Asks the site for the manual, sending the kept copy's ETag; a new one replaces the copy on disk. */
    @Synchronized
    fun refresh(context: Context, chinese: Boolean = false, url: String = if (chinese) DocsApi.ZH_URL else DocsApi.URL): Refresh = runCatching {
        val base = if (chinese) bundledChinese(context)?.rev else null
        val sameBase = !chinese || (base != null && chineseBaseFile(context).takeIf { it.isFile }?.readText() == base)
        val etag = etagFile(context, chinese).takeIf { sameBase && it.isFile && manualFile(context, chinese).isFile }?.readText()
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 8000
            readTimeout = 12000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "PokeDaisy/${BuildConfig.VERSION_NAME}")
            etag?.let { setRequestProperty("If-None-Match", it) }
        }
        when (conn.responseCode) {
            HttpURLConnection.HTTP_NOT_MODIFIED -> Refresh.Unchanged
            HttpURLConnection.HTTP_OK -> {
                val body = conn.inputStream.use { it.readBytes().toString(Charsets.UTF_8) }
                if (chinese) require(JSONObject(body).optString("locale") == "zh-Hans") { "wrong manual language" }
                val manual = DocsApi.parse(body)
                val tmp = File(dir(context), if (chinese) "docs-zh-hans.json.part" else "docs.json.part")
                tmp.writeText(body)
                java.nio.file.Files.move(tmp.toPath(), manualFile(context, chinese).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING)
                if (chinese && base != null) chineseBaseFile(context).writeText(base)
                conn.getHeaderField("ETag")?.let { etagFile(context, chinese).writeText(it) } ?: etagFile(context, chinese).delete()
                Refresh.Updated(manual)
            }
            else -> Refresh.Failed(offline = false)
        }
    }.getOrElse { Refresh.Failed(offline = it is java.io.IOException) }

    /** An image block's picture: from disk if it was shown before, else fetched from the site and kept. */
    fun image(context: Context, manual: DocsApi.Manual, image: DocsApi.Block.Image): Bitmap? = runCatching {
        val file = imageFile(context, image)
        if (!file.isFile) {
            val relative = image.src.removePrefix("/docs/")
            if (image.src.startsWith("/docs/") && relative.matches(Regex("(shots|art)/[a-z0-9/-]+\\.png"))) {
                val bundled = runCatching { context.assets.open("docs-zh-hans/$relative").use { it.readBytes() } }.getOrNull()
                if (bundled != null) {
                    val hash = MessageDigest.getInstance("SHA-1").digest(bundled).joinToString("") { "%02x".format(it) }.take(10)
                    if (hash == image.version) return@runCatching BitmapFactory.decodeByteArray(bundled, 0, bundled.size)
                }
            }
            val conn = (URL(manual.site + image.src).openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 15000
                setRequestProperty("User-Agent", "PokeDaisy/${BuildConfig.VERSION_NAME}")
            }
            if (conn.responseCode != HttpURLConnection.HTTP_OK) return@runCatching null
            val tmp = File(file.parentFile, "${file.name}.part")
            conn.inputStream.use { input -> tmp.outputStream().use { input.copyTo(it) } }
            tmp.renameTo(file)
        }
        BitmapFactory.decodeFile(file.path)
    }.getOrNull()

    private fun imageFile(context: Context, image: DocsApi.Block.Image): File {
        val name = MessageDigest.getInstance("SHA-1").digest((image.src + "@" + image.version).toByteArray())
            .joinToString("") { "%02x".format(it) }.take(20) + ".png"
        return File(File(dir(context), "img").apply { mkdirs() }, name)
    }

    @Volatile private var prefetched = false

    /** Once per process, in the background: asks for a newer manual, so HELP has one even offline later. */
    fun prefetch(context: Context) {
        if (prefetched) return
        prefetched = true
        val app = context.applicationContext
        Thread({ (refresh(app) as? Refresh.Updated)?.let { pruneImages(app, it.manual) } }, "docs-prefetch").apply { isDaemon = true }.start()
    }

    /** Deletes kept pictures the manual no longer shows (an old version of a screenshot). */
    fun pruneImages(context: Context, manual: DocsApi.Manual) {
        val keep = mutableSetOf<String>()
        fun walk(blocks: List<DocsApi.Block>) {
            for (b in blocks) when (b) {
                is DocsApi.Block.Image -> keep += imageFile(context, b).name
                is DocsApi.Block.Bullets -> b.items.forEach(::walk)
                is DocsApi.Block.Note -> walk(b.blocks)
                else -> {}
            }
        }
        manual.pages.forEach { walk(it.blocks) }
        File(dir(context), "img").listFiles()?.filter { it.name !in keep }?.forEach { it.delete() }
    }
}
