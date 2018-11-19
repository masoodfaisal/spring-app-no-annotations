# Writing Spring Applications using Kofu

> Programs must be written for people to read, and only incidentally for machines to execute. — [Abelson and Sussman](https://en.wikiquote.org/wiki/Programming_languages)

The way I write spring applications comprised of heavy use of Spring annotations (in olden days it was all in spring bean xml). 
The problem with this approach is that the partial flow of the application is controlled by annotations. I need to understand them
and look back to documentation to understand their behaviour. By reading just the code, it is difficult to predict the flow of control.
Another issue is controlled class loading which always confuses me as you never know (unless you read additional documentation or learnt the hard way)
what is being loaded and what is not.

Luckily, Spring has a new way to code to and they have called it Spring Functional or SpringFu. Using [Kotlin](https://kotlinlang.org)  in this bog, I will showcase the difference between the new and the old approach.


Let's a simple spring based application using the annotations
@SpringBootApplication does [lots](https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-using-springbootapplication-annotation.html)
of stuff. Surely this information is not captured in the code below. So the code provided below does other things which are hidden and thats need to be
improved.

When we go further, [@RestController](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RestController.html) does a series of things which
complicates the readability of code i.e. I need to look at different places other than my code to understand what is it doing.

@RequestMapping, @Autowired, @Component are all adding up to the problem I have mentioned above. Spring has to use Reflection, Kotlin open classes (see the pom file) and all kind of magic to make this simler looking code to work.
What if my goal to read the code and only code and it helps me understand whats going on without the voodoo.

```kotlin
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
class EventService {

    fun getAllEvents() : List<Event>{
        return listOf(
                Event(name = "event1", description = "desc1"),
                Event(name = "event2", description = "desc2")
        )
    }
}

data class Event (val name: String, val description: String)

``` 
 
 Using KoFu, we can write code that is more readable (and simple with less libraries - check the pom file diff section).
 I can read the code and jsut this file tells me whats going on
 - I have a main function which loads my aapSimpl dsl.
 - This dsl imports the beans (which i load in the eventBeans dsl) and configure what server should do using the eventroutes dsl
 
```kotlin
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
```
 
From the example above, it is evident that coroutines provides a better alternative to write more readable code. The reactive code I have mentioned can be optimised to reduce more clutter, but there will always be reactive specific classes which I can avoid now.

[Kotlin](https://kotlinlang.org/docs/reference/) is an exciting new programming language specially if you are coming from java background. You can start your kotlin journey by attending this [coursera course](https://www.coursera.org/learn/kotlin-for-java-developers).
 



This project uses the following technologies. 


