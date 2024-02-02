package cz.glubo

import io.github.oshai.kotlinlogging.KotlinLogging
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.scheduling.annotation.Scheduled
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
    val length: Int,
) {
    val id: String
        get() = "$vhost:$queue"
}

@Controller
class DeadQueuesController(
    private val deadQueuesService: DeadQueuesService,
) {
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get("/dlqs")
    fun dlqs(principal: Principal) =
        ModelAndView(
            "dlqs",
            mapOf<String, Any>(
                "dlqs" to deadQueuesService.getDlqs(),
            ),
        )

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/dlqs/{deadQueueId}/bucket")
    fun bucket(
        principal: Principal,
        @PathVariable deadQueueId: String,
    ): ModelAndView<Map<String, Any>> {
        deadQueuesService.bucket(deadQueueId)
        return ModelAndView(
            "dlqs",
            mapOf<String, Any>(
                "dlqs" to deadQueuesService.getDlqs(),
            ),
        )
    }
}

@Singleton
class DeadQueuesService {
    val logger = KotlinLogging.logger { }
    private val dlqs =
        mutableListOf(
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

    fun getDlqs(): List<DeadQueue> {
        logger.info { "Requested dlq list" }
        return dlqs.sortedBy { it.id }
    }

    fun bucket(deadQueueId: String) {
        logger.info { "Requested dlq bucket of $deadQueueId" }
        val dlq =
            dlqs.firstOrNull { it.id == deadQueueId }
                ?: throw DlqNotFoundException(deadQueueId)

        dlqs.remove(dlq)
        dlqs.add(dlq.copy(length = 0))

        logger.debug { "Dlq $deadQueueId bucketted, resulting dlqs: $dlqs" }
    }

    @Scheduled(fixedDelay = "1s")
    fun addDead() {
        val dlq = dlqs.random()
        dlqs.remove(dlq)
        dlqs.add(dlq.copy(length = dlq.length + 1))
    }

    class DlqNotFoundException(deadQueueId: String) : RuntimeException("Dead Letter Queue $deadQueueId not found")
}
