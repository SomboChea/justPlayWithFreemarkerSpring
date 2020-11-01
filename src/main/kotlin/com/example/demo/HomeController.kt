package com.example.demo

import com.example.demo.JsonUtils.getFieldsFromJsonNode
import com.example.demo.JsonUtils.toJsonNode
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@Controller
@RequestMapping
class HomeController {
    @GetMapping
    fun index(model: Model): String {
        val data: List<Employee> = listOf(
                Employee(1, "Sambo", "Software Developer"),
                Employee(2, "Neli", "Developer Specialized"),
                Employee(3, "Chea", "Hello World"),
        )
        val builder = ReportBuilder(data)
        model.addAttribute("data", builder)
        return "home"
    }

    @GetMapping("/raw")
    @ResponseBody
    fun indexRaw(
            @RequestParam(value = "body", defaultValue = "Default") value: String,
    ): Any {
        val data: List<Employee> = listOf(
                Employee(1, "Sambo", "Software"),
                Employee(3, "Chea", "Hello World"),
        )

        val builder = ReportBuilder(data)
        val raw = FreeMakerUtils.convertStringHtmlToTemplate(sourceCode = value)
        return FreeMakerUtils.convertToString(raw, mapOf(
                "name" to "Sambo",
                "data" to builder,
                "emp" to Employee(1, "Sambo", "Software Developer"),
        ))
    }

    @GetMapping("/request")
    @ResponseBody
    fun getRequestTemplate(
            @RequestBody request: ReportTemplateRequest
    ): Any? {
        val view = request.toResponse()
        return view.getOutput()
    }
}

@RestController
@RequestMapping("/api")
class HomeApiController @Autowired constructor(
        private val applicationContext: ApplicationContext
) {
    @GetMapping("/invoker")
    fun invoker(
            @RequestParam(value = "calling", defaultValue = "false") calling: Boolean,
            @RequestParam(value = "method", defaultValue = "generate") method: String,
    ): Any? {
        return AppContextUtils.invoker(
                "myGenerator",
                method,
                listOf(Boolean::class.java),
                listOf(calling),
        )
    }

    @GetMapping
    fun api(): Any {
        val data: List<Employee> = listOf(
                Employee(1, "Sambo", "Software Developer"),
                Employee(2, "Neli", "Developer Specialized"),
        )

        return data.toJsonNode()
    }

    @GetMapping("/fields")
    fun apiFields(): Any {
        val data: List<Employee> = listOf(
                Employee(1, "Sambo", "Software Developer", 150.0),
                Employee(2, "Neli", "Developer Specialized"),
        )

        return data
                .toJsonNode()
                .findValues("salary").sumByDouble { it.asDouble() }
    }
}

class ReportBuilder<T>(
        private var data: List<T>,
) {
    private val configs: MutableMap<String, String> = mutableMapOf(
            "title" to "My World",
            "description" to "Hello, just added to world!",
            "footerWord" to "My Footer",
            "footer" to "Hello Footer",
    )

    @JvmOverloads
    fun getConfig(key: String, defaultValue: String = "") = configs.getOrDefault(key, defaultValue)

    private var _columns: List<String>? = null
    fun getColumns(): List<String> {
        if (_columns == null) {
            _columns = data
                    .toJsonNode()
                    .getFieldsFromJsonNode()
        }
        return _columns ?: emptyList()
    }

    fun getAggregate(functionName: String, field: String): Any? {
        return when (functionName) {
            "sum" -> {
                data.toJsonNode()
                        .findValues(field)
                        .sumBy { it.asInt() }
            }
            "sumDouble" -> {
                data.toJsonNode()
                        .findValues(field)
                        .sumByDouble { it.asDouble() }
            }
            "sumDecimal" -> {
                var value = BigDecimal.ZERO
                getData().toJsonNode()
                        .findValues(field)
                        .forEach {
                            value = value.plus(it.decimalValue())
                        }
                value
            }
            else -> "-1"
        }
    }

    fun getData(): List<T> {
        return data
    }
}

class Employee(
        val id: Long,
        val name: String,
        val position: String,
        val salary: Double = 100.0,
)

object JsonUtils {
    private val mapper = ObjectMapper()
    fun getObjectMapper(): ObjectMapper {
        return mapper
    }

    fun Any?.toJsonString(): String? {
        if (this == null) return null
        return getObjectMapper().writeValueAsString(this)
    }

    fun Any?.toJsonNode(): JsonNode {
        return getObjectMapper().readTree(
                if (this is String) {
                    this
                } else this.toJsonString()
        )
    }

    fun JsonNode.getFieldsFromJsonNode(): List<String> {
        val fields = mutableListOf<String>()
        val i = this.elementAtOrNull(0)?.fieldNames()
        i?.forEachRemaining {
            fields.add(it)
        }
        return fields
    }
}