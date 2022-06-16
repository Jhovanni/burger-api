package com.jhovanni.burgerapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class BurgerApiApplication

fun main(args: Array<String>) {
    runApplication<BurgerApiApplication>(*args)
}
