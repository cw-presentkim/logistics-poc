package com.coway.america.example

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.junitxml.JunitXmlReporter
import io.kotest.extensions.spring.SpringExtension

/**
 * kotest project custom config
 *
 * @see <a href="https://kotest.io/docs/extensions/junit_xml.html">JUnit XML Format Reporter</a>
 */
class ProjectTestConfig : AbstractProjectConfig() {

    override fun extensions() = listOf(
        SpringExtension,
        JunitXmlReporter(
            includeContainers = false,
            useTestPathAsName = true
        )
    )
}
