package com.coway.america.example.model.response

import com.fasterxml.jackson.annotation.JsonProperty

data class DefaultResponse<T>(
    @JsonProperty("code")
    val code: String = "OK",
    @JsonProperty("message")
    val message: String = "",
    @JsonProperty("data")
    val data: T? = null
)
