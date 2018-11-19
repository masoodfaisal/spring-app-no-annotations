package com.faisal.spring.demo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class SpringWithAnnotationsApplication

fun main(args: Array<String>) {
    runApplication<SpringWithAnnotationsApplication>(*args)
}



@RestController
@RequestMapping("/events")
class EventHandler {

    @Autowired
    lateinit var eventService: EventService

    @GetMapping("/")
    fun getAllEvents () : List<Event> {
        return eventService.getAllEvents()
    }

}

@Component
open class EventService {

    fun getAllEvents() : List<Event>{
        return listOf(
                Event(name = "event1", description = "desc1"),
                Event(name = "event2", description = "desc2")
        )
    }
}

data class Event (val name: String, val description: String)