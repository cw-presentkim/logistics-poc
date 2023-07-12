package com.coway.america.example

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.TestContainerExtension
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.BindMode

@SpringBootTest
@TestPropertySource(properties = ["spring.datasource.url=jdbc:postgresql://localhost:35432/cw_america"])
class ExampleApplicationTests: FunSpec({
    install(TestContainerExtension("public.ecr.aws/docker/library/postgres:15.3")) {
        portBindings = listOf("35432:5432")
        addEnv("POSTGRES_PASSWORD", "postgres")
        addFileSystemBind("local-env/initdb.sh", "/docker-entrypoint-initdb.d/initdb.sh", BindMode.READ_ONLY)
    }

    test("contextLoads") {
        1 shouldBe 1
    }
})
