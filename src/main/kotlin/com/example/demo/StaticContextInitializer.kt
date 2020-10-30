package com.example.demo

import freemarker.template.Configuration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Lazy(value = false)
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
class StaticContextInitializer @Autowired constructor(
        private val freeMakerConfig: Configuration,
) {
    @PostConstruct
    fun init() {
        FreeMakerUtils.setFreeMakerTemplateConfig(freeMakerConfig)
    }
}