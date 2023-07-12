package com.coway.america.example.model.comment

import com.fasterxml.jackson.annotation.JsonProperty

data class CommentListItem(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("contents")
    val contents: String,
    @JsonProperty("viewCount")
    val viewCount: Int
)
