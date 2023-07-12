package com.coway.america.example.model.comment

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime

data class CommentDetailItem(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("contents")
    val contents: String,
    @JsonProperty("viewCount")
    val viewCount: Int,
    @JsonProperty("createdDateTime")
    val createdDateTime: ZonedDateTime
)
