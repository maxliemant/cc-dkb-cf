package de.maxliemant.ccdkb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CcDkbApplication

fun main(args: Array<String>) {
    runApplication<CcDkbApplication>(*args)
}
