package com.coway.america.example.model.comment

import com.fasterxml.jackson.annotation.JsonProperty

data class CommentCreateItem(
    @JsonProperty("contents")
    val contents: String
)
