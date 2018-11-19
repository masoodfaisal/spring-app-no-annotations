package com.faisal.spring.demo.springwithannotations


import com.faisal.spring.demo.Event
import com.faisal.spring.demo.appSimple
import org.junit.Test
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.test.test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpringWithNoAnnotationsTests {

    private val client = WebClient.create("http://localhost:8080")

    @BeforeAll
    fun beforeAll() {
        appSimple.run()
    }

    @Test
    fun `Test Get all the Events`() {
        client.get().uri("/events")
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono<String>()
                .test()
                .expectNextCount(1)
                .expectComplete()
                .verify()
    }

}
