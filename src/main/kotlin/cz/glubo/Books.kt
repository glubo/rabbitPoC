package cz.glubo

import io.github.serpro69.kfaker.Faker
import io.micronaut.context.annotation.Infrastructure
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.serde.annotation.Serdeable
import io.micronaut.views.ModelAndView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList

@Serdeable
data class Book(val name: String) {
    var id: Long? = null
}

@Infrastructure
class BookRepository {
    val books = mutableListOf<Book>()

    fun findAll(): Flow<Book> = books.asFlow()

    fun save(book: Book) =
        books.add(
            book.apply { id = books.size.toLong() },
        )
}

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller
class BookController(
    private val bookRepository: BookRepository,
) {
    private val faker = Faker()

    @Get("/")
    fun home() =
        ModelAndView(
            "home",
            emptyMap<String, Any>(),
        )

    @Get("/books")
    suspend fun books() =
        ModelAndView(
            "books",
            mapOf<String, Any>(
                "books" to bookRepository.findAll().toList(),
            ),
        )

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/books/random")
    suspend fun addRandomBook(): ModelAndView<Map<String, Any>> {
        bookRepository.save(
            Book(
                name = faker.book.title(),
            ),
        )
        return ModelAndView(
            "books",
            mapOf<String, Any>(
                "books" to bookRepository.findAll().toList(),
            ),
        )
    }
}
