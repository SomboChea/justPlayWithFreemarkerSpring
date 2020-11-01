package com.example.demo

import com.example.demo.JsonUtils.toJsonNode
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.node.JsonNodeType
import freemarker.template.Template
import java.io.Serializable

data class ReportTemplateRequest(
        var template: TemplateSource? = null,
        var dataSource: DataSourceTemplate? = null,
        var options: Map<String, Any?>? = null,
) : Serializable {
    fun toResponse(): ReportTemplateResponse {
        return ReportTemplateResponse(
                template, dataSource, options
        )
    }
}

data class ReportTemplateResponse(
        var template: TemplateSource? = null,
        var dataSource: DataSourceTemplate? = null,
        var options: Map<String, Any?>? = null,
) : Serializable {
    fun getReportBuilder(): ReportBuilder<Any?> {
        return ReportBuilder(
                data = dataSource?.getData() ?: emptyList()
        )
    }

    fun getOutput(): String? {
        val model = mutableMapOf<String, Any?>("data" to getReportBuilder())
        model.putAll(options ?: emptyMap())
        return template?.toHtmlString(model)
    }
}

class TemplateSource constructor(
        @JsonProperty var name: String?,
        @JsonProperty var source: String?,
) : Serializable {
    @JsonIgnore
    fun toTemplate(): Template? {
        if (source.isNullOrEmpty()) return null
        return FreeMakerUtils.convertStringHtmlToTemplate(name ?: "temp", source ?: "")
    }

    fun toHtmlString(model: Map<String, Any?>): String {
        val temp = toTemplate() ?: return ""
        return FreeMakerUtils.convertToString(temp, model)
    }
}

enum class DataSourceTemplateType {
    SQL,
    REST,
    JSON,
}

class DataSourceTemplate(
        var type: DataSourceTemplateType? = null,
        var source: String? = null,
) : ReportDataSource {
    override fun getData(): List<Any?> {
        return when (type) {
            DataSourceTemplateType.SQL -> emptyList()
            DataSourceTemplateType.REST -> emptyList()
            DataSourceTemplateType.JSON -> {
                val json = source.toJsonNode()
                if (json.nodeType == JsonNodeType.ARRAY) {
                    json.map { it }
                } else {
                    listOf(json)
                }
            }
            else -> emptyList()
        }
    }
}

interface ReportDataSource {
    fun getData(): List<Any?>
}