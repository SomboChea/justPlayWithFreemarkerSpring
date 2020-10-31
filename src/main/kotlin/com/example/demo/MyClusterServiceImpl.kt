package com.example.demo

import org.springframework.stereotype.Service
import java.util.*

@Service
class MyClusterServiceImpl : MyClusterService {
    override fun getCluster(): Any? {
        return "Cluster ID: ${UUID.randomUUID()}"
    }
}