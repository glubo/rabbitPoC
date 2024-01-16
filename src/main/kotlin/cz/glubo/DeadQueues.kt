package cz.glubo

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.views.ModelAndView
import jakarta.inject.Singleton
import java.security.Principal
import kotlin.random.Random

data class DeadQueue(
    val vhost: String,
    val dlq: String,
    val queue: String,
    val length: Int?,
)

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller
class DeadQueuesController(
    private val deadQueuesService: DeadQueuesService,
) {
    @Get("/dlqs")
    fun dlqs(principal: Principal) =
        ModelAndView(
            "dlqs",
            mapOf<String, Any>(
                "dlqs" to deadQueuesService.getDlqs(principal.getRoles()),
            ),
        )
}

private fun Principal.getRoles(): List<String> =
    when (this) {
        else -> emptyList()
    }

@Singleton
class DeadQueuesService {
    fun getDlqs(roles: List<String>) =
        listOf(
            DeadQueue(
                vhost = "payments",
                dlq = "nejake_fronta_todle_je_dlouhy.dlq",
                queue = "nejake_fronta_todle_je_dlouhy",
                length = 1,
            ),
            DeadQueue(
                vhost = "payments",
                dlq = "aaa_fronta_todle_je_dlouhy.dlq",
                queue = "aaa_fronta_todle_je_dlouhy",
                length = Random.nextInt(1, 100),
            ),
            DeadQueue(
                vhost = "payments",
                dlq = "bbb_fronta_todle_je_dlouhy.dlq",
                queue = "bbb_fronta_todle_je_dlouhy",
                length = 4,
            ),
            DeadQueue(
                vhost = "payments",
                dlq = "ccc_fronta_todle_je_dlouhy.dlq",
                queue = "ccc_fronta_todle_je_dlouhy",
                length = Random.nextInt(1, 100),
            ),
            DeadQueue(
                vhost = "payments",
                dlq = "ddd_fronta_todle_je_dlouhy.dlq",
                queue = "ddd_fronta_todle_je_dlouhy",
                length = 6,
            ),
            DeadQueue(
                vhost = "payments",
                dlq = "eee_fronta_todle_je_dlouhy.dlq",
                queue = "eee_fronta_todle_je_dlouhy",
                length = Random.nextInt(1, 100),
            ),
        )
}
