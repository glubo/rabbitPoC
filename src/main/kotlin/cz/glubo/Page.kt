package cz.glubo

data class Page(
    val description: String? = null,
    val title: String,
    val uri: String,
)

object Site {
    val pages =
        listOf(
            Page(
                description = "Welcome page",
                title = "Home",
                uri = "/",
            ),
            Page(
                description = "This would be Books description",
                title = "Dead Letter Queues",
                uri = "/dlqs",
            ),
        )
    val pagesByTitle = pages.associateBy { it.title }

    fun page(title: String) = pagesByTitle[title]!!
}
