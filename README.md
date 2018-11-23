# Writing [better] Spring Applications using Kofu

> Truth can only be found in one place: the code. - [Robert C. Martin, Clean Code: A Handbook of Agile Software Craftsmanship](https://www.goodreads.com/book/show/3735293-clean-code)

The way we strucutre our code has a direct impact on how understable is it. Code that is easy to follow with no or less hidden functionality is much easier to maintain. It also make it easier for our fellow programmers to track down the bugs in the code. This also helps us to avoid Venkat's [JDD](https://aidium.se/2015/06/tdd-with-venkat/).

The way I write spring applications comprised of heavy use of Spring [annotations](https://springframework.guru/spring-framework-annotations/) (in olden days it was all in spring bean xml). 
The problem with this approach is that the partial flow of the application is controlled by annotations. The full flow of my code is not in one place i.e. my code. I need to look back to the documentation to understand the annotations behaviour. By reading just the code, it is difficult to predict the flow of control.

Another issue is controlled class loading which always confuses me as you never know (unless you read additional documentation or learnt the hard way) what is being loaded and what is not.

## SpringFu to the rescue
Luckily, Spring has a new way to code to and it has been called as Spring Functional or [SpringFu](https://github.com/spring-projects/spring-fu). Using [Kotlin](https://kotlinlang.org)  I will showcase some of the benefits you get from SpringFu.

Let's start with a simple spring based application using the annotations approach. The first annotation we meet is
@SpringBootApplication and it does [lots](https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-using-springbootapplication-annotation.html)
of things. Surely this information is not captured in the code when you use this annotation. 

When we go further in the example provided below, [@RestController](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RestController.html) does a series of things which
complicates the readability of code i.e. I need to look at different places other than current code to understand what is it doing.

@RequestMapping, @Autowired, @Component are all adding up to the problem I have mentioned above. Spring has to use Reflection, 
Kotlin [open](https://kotlinlang.org/docs/reference/classes.html#inheritance) classes and all kind of facilities to make this code work. 

What if my goal is to read the code and only code to understand what is it doing. Reducing the usage of Reflection will also help us in moving towards [GraalVM](https://www.graalvm.org) based native code.

First take a look at the code with annotations and try to see if you can get how the control flows in it.

You can get full example at my [github](https://github.com/masoodfaisal/spring-app-no-annotations) repo.

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
 
Go to stackoverflow and you can find a lot of [questions](https://stackoverflow.com/questions/49018415/proper-configuration-of-spring-boot-2-and-junit-5) about the right way of using annotations. The number and variety of questions will give you an idea of how important the code readability is.

> Programs must be written for people to read, and only incidentally for machines to execute. — [Abelson and Sussman](https://en.wikiquote.org/wiki/Programming_languages)

 Now let's try to write the same code using the SpringFu approach. The code that is more readable and I can follow the full flow by just reading it.
 The code after this section does use the flow as follows. 
 - I have a main function which loads my appSimple DSL.
 - This dsl imports the beans (which I load in the eventBeans DSL) and configure what server should do using the eventroutes DSL.
 
```kotlin
 //create an application
 //remember no @SpringBootApplication
 fun main(args: Array<String>) {
     appSimple.run()
 }
 
 //using the application dsl, i define what my service will listen to
 val appSimple = application {
     //use the beans i define
     import(eventBeans)

    //http server listens for this endpoint
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
 
 
 //define on what endpont i would be listening
 //@RestController
 //@RequestMapping("/events")
 //@GetMapping("/")
 fun eventRoutes(handler: EventHandler) = router {
     "/events".nest {
         GET("/", handler::generateResponse)
     }
 }
 
 //create the handler for http request
 class EventHandler(private val eventService: EventService) {
     fun generateResponse(request: ServerRequest) = ServerResponse.ok().body(
             BodyInserters.fromObject(eventService.getAllEvents())
     )
 }
 
 //business logic
 // no need to @Component
 class EventService {
     fun getAllEvents(): List<Event> {
         return mutableListOf(
                 Event(name = "event1", description = "desc1"),
                 Event(name = "event2", description = "desc2")
         )
     }
 }
 
 data class Event(val name: String, val description: String)
```

## Avoid Kotlin's Open classes
When using the annoations, we need to make all classes [open](https://kotlinlang.org/docs/reference/compiler-plugins.html) for spring to work. See the pom section below from the . With the KoFu there is no need for this. 

```xml
<plugin>
				<artifactId>kotlin-maven-plugin</artifactId>
				<groupId>org.jetbrains.kotlin</groupId>
				<configuration>
					<args>
						<arg>-Xjsr305=strict</arg>
					</args>
					<compilerPlugins>
						<plugin>spring</plugin>
					</compilerPlugins>
				</configuration>
				<dependencies>
					<dependency>
						<groupId>org.jetbrains.kotlin</groupId>
						<artifactId>kotlin-maven-allopen</artifactId>
						<version>${kotlin.version}</version>
					</dependency>
				</dependencies>
			</plugin>
```

[SpringFu](https://github.com/spring-projects/spring-fu) is an exciting new project which enables us to write cleaner, more readable code and offer more control while writing spring based applications. One of the exciting goals is to able to write native applications usng java by supporting [GraalVM](https://www.graalvm.org). Be aware that some of the [SpringFu](https://github.com/spring-projects/spring-fu) components are not production ready yet.  

[Kotlin](https://kotlinlang.org/docs/reference/) is an exciting new programming language specially if you are coming from java background. You can start your kotlin journey by attending this [coursera course](https://www.coursera.org/learn/kotlin-for-java-developers).
 




