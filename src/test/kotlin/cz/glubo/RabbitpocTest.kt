package cz.glubo

import io.kotest.core.spec.style.StringSpec
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest

@MicronautTest
class RabbitpocTest(private val application: EmbeddedApplication<*>) : StringSpec({

    "test the server is running" {
        assert(application.isRunning)
    }
})
