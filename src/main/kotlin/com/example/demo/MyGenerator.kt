package com.example.demo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import kotlin.random.Random

@Component
class MyGenerator @Autowired constructor(
        private val myClusterService: MyClusterService,
) {
    @JvmOverloads
    fun generate(calling: Boolean = false): String {
        if (calling) return Random(1000).nextInt().toString()
        return UUID.randomUUID().toString()
    }

    fun getCluster(): Any? {
        return myClusterService.getCluster()
    }
}