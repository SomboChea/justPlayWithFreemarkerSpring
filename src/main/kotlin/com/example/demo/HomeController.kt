package com.example.demo

import com.example.demo.JsonUtils.getFieldsFromJsonNode
import com.example.demo.JsonUtils.toJsonNode
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import freemarker.template.Configuration
import freemarker.template.DefaultObjectWrapper
import freemarker.template.Template
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

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
        val cfg = Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS)
        cfg.objectWrapper = DefaultObjectWrapper(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS)

        val t = Template("temp.ftl", value, cfg)

        val builder = ReportBuilder(data)
        val raw = FreeMakerUtils.convertStringHtmlToTemplate(sourceCode = value)
        return FreeMakerUtils.convertToString(raw, mapOf(
                "name" to "Sambo",
                "data" to builder,
                "emp" to Employee(1, "Sambo", "Software Developer"),
        ))
    }
}

@RestController
@RequestMapping("/api")
class HomeApiController {
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
                Employee(1, "Sambo", "Software Developer"),
                Employee(2, "Neli", "Developer Specialized"),
        )

        return data
                .toJsonNode()
                .getFieldsFromJsonNode()
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

    fun getData(): List<T> {
        return data
    }
}

class Employee(
        val id: Long,
        val name: String,
        val position: String,
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
        return getObjectMapper().readTree(this.toJsonString())
    }

    fun JsonNode.getFieldsFromJsonNode(): List<String> {
        val fields = mutableListOf<String>()
        val i = this.elementAtOrNull(0)?.fieldNames()
        i?.forEachRemaining {
            println(it)
            fields.add(it)
        }
        return fields
    }
}