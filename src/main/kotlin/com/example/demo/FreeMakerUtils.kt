package com.example.demo

import freemarker.cache.TemplateLoader
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils
import java.io.Reader
import java.io.StringReader
import java.io.StringWriter
import java.util.*

object FreeMakerUtils {
    private var freeMakerConfig: Configuration? = null
    fun setFreeMakerTemplateConfig(freeMakerConfiguration: Configuration) {
        freeMakerConfig = freeMakerConfiguration
    }

    private fun getConfiguration(): Configuration {
        freeMakerConfig!!.locale = Locale.US
        return freeMakerConfig!!
    }

    fun getTemplate(templateUri: String): Template {
        return getConfiguration().getTemplate(templateUri)
                ?: throw Exception("no template found $templateUri")
    }

    fun convertTemplateToHtmlString(template: Template, model: Map<String, Any?> = mutableMapOf()): String {
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
    }

    fun convertToString(template: Template, model: Map<String, Any?> = emptyMap()): String {
        val stringWriter = StringWriter()
        template.process(model, stringWriter)
        val str = stringWriter.toString()
        stringWriter.close()
        return str
    }


    fun convertToTemplate(contentHtml: String): Template {
        val oldConfig = getConfiguration().templateLoader
        getConfiguration().templateLoader = CustomTemplateLoader()
        val data = getConfiguration().getTemplate(contentHtml, null, "UTF-8", true, true)
        getConfiguration().templateLoader = oldConfig
        return data
    }
}

class CustomTemplateLoader : TemplateLoader {
    override fun closeTemplateSource(templateSource: Any?) {

    }

    override fun getReader(templateSource: Any?, encoding: String?): Reader {
        return StringReader(templateSource.toString())
    }

    override fun getLastModified(templateSource: Any?): Long {
        return Date().time
    }

    override fun findTemplateSource(name: String?): Any {
        return name.toString()
    }

}