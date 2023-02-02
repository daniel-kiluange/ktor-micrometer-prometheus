package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

const val applicationPort = 8080
const val managementPort = 8081

suspend fun main() {
    coroutineScope {
        launch {
            setupServer(applicationPort,false){
                basicModule()
                httpModule()
            }
        }
        launch {
            setupServer(managementPort,true){
                basicModule()
                routing {
                    get("/metrics") {
                        call.respond(appMicrometerRegistry.scrape())
                    }
                    get("/health"){
                        call.respond(mapOf("status" to "UP"))
                    }
                }
            }
        }
    }
}

fun Application.basicModule() {
    configureMonitoring()
    configureSerialization()
}

fun Application.httpModule() {
    configureRouting()
}


fun setupServer(port:Int,waitStart:Boolean,context: Application.()->Unit) = embeddedServer(Netty,port){
    context()
}.start(waitStart)
