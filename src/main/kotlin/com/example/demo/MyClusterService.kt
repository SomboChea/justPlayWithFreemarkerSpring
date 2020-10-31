package com.example.demo

import org.springframework.stereotype.Service

@Service
interface MyClusterService {
    fun getCluster(): Any?
}