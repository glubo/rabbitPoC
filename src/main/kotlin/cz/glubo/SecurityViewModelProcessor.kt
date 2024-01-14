package cz.glubo

import io.micronaut.context.annotation.Infrastructure
import io.micronaut.http.HttpRequest
import io.micronaut.views.ModelAndView
import io.micronaut.views.model.ViewModelProcessor

@Infrastructure
class SecurityViewModelProcessor internal constructor() : ViewModelProcessor<Map<String?, Any?>?> {
    override fun process(
        request: HttpRequest<*>,
        modelAndView: ModelAndView<Map<String?, Any?>?>,
    ) {
        val principal = request.userPrincipal.orElse(null)
        val originalModel = modelAndView.model.orElseGet { null } ?: emptyMap()

        modelAndView.setModel(
            originalModel.plus(
                mapOf<String?, Any?>(
                    "username" to (principal?.name ?: "Anonymous"),
                    "loggedIn" to (principal != null),
                ),
            ),
        )
    }
}
