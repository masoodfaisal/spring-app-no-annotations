package com.faisal.spring.demo

import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.web.jackson
import org.springframework.boot.kofu.web.server
import org.springframework.context.support.beans
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

//create an application
//remember no @SpringBootApplication
fun main(args: Array<String>) {
    appSimple.run()
}

//using the application dsl,
val appSimple = application {
    import(eventBeans)

    server {
        import(::eventRoutes)

        codecs {
            jackson()
        }
    }
}

//define beans
val eventBeans = beans {
    bean<EventService>()
    bean<EventHandler>()
}

//create the handler for http request
//
//@RestController
//@RequestMapping("/events")
//@GetMapping("/")

fun eventRoutes(handler: EventHandler) = router {
    "/events".nest {
        GET("/", handler::generateResponse)
    }
}

class EventHandler(private val eventService: EventService) {
    fun generateResponse(request: ServerRequest) = ServerResponse.ok().body(
            BodyInserters.fromObject(eventService.getAllEvents())
    )
}


//business logic
// no need to @Component
class EventService {

    fun getAllEvents(): List<Event> {
        return listOf(
                Event(name = "event1", description = "desc1"),
                Event(name = "event2", description = "desc2")
        )
    }
}

data class Event(val name: String, val description: String)


