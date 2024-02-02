package cz.glubo

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.views.ModelAndView

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller
class HomeController {
    @Get("/")
    fun home() =
        ModelAndView(
            "home",
            emptyMap<String, Any>(),
        )
}
