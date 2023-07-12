package com.coway.america.example.controller

import com.coway.america.example.Constants
import com.coway.america.example.model.comment.CommentCreateItem
import com.coway.america.example.model.comment.CommentDetailItem
import com.coway.america.example.model.comment.CommentListItem
import com.coway.america.example.model.comment.CommentUpdateItem
import com.coway.america.example.model.response.DefaultResponse
import com.coway.america.example.service.CommentService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * 댓글 REST API Endpoint.
 */
@RestController
class CommentController(
    private val commentService: CommentService
) {

    /**
     * 댓글 목록 조회.
     */
    @PreAuthorize("@accessControl.hasPermission('${Constants.RESOURCE_ID_COMMENT}', '${Constants.ACTION_ID_VIEW}')")
    @PostMapping("/comments")
    fun fetchCommentList(): DefaultResponse<List<CommentListItem>> {
        return DefaultResponse(
            data = commentService.getCommentList()
        )
    }

    @PreAuthorize("@accessControl.hasPermission('${Constants.RESOURCE_ID_COMMENT}', '${Constants.ACTION_ID_CREATE}')")
    @PostMapping("/comment")
    fun createCommentDetail(
        authentication: Authentication,
        @RequestBody
        createItem: CommentCreateItem
    ): DefaultResponse<CommentDetailItem> {
        return DefaultResponse(
            data = commentService.createComment(createItem, authentication.name)
        )
    }

    @PreAuthorize("@accessControl.hasPermission('${Constants.RESOURCE_ID_COMMENT}', '${Constants.ACTION_ID_VIEW}')")
    @GetMapping("/comment/{commentId}")
    fun fetchCommentDetail(
        authentication: Authentication,
        @PathVariable("commentId")
        commentId: String
    ): DefaultResponse<CommentDetailItem> {
        return DefaultResponse(
            data = commentService.getCommentDetail(commentId, authentication.name)
        )
    }

    @PreAuthorize("@accessControl.hasPermission('${Constants.RESOURCE_ID_COMMENT}', '${Constants.ACTION_ID_UPDATE}')")
    @PutMapping("/comment/{commentId}")
    fun updateCommentDetail(
        authentication: Authentication,
        @PathVariable("commentId")
        commentId: String,
        @RequestBody
        updateItem: CommentUpdateItem
    ): DefaultResponse<CommentDetailItem> {
        return DefaultResponse(
            data = commentService.updateCommentDetail(commentId, updateItem, authentication.name)
        )
    }

    @PreAuthorize("@accessControl.hasPermission('${Constants.RESOURCE_ID_COMMENT}', '${Constants.ACTION_ID_DELETE}')")
    @DeleteMapping("/comment/{commentId}")
    fun deleteCommentDetail(
        authentication: Authentication,
        @PathVariable("commentId")
        commentId: String
    ): DefaultResponse<Any> {
        commentService.deleteComment(commentId, authentication.name)
        return DefaultResponse()
    }
}
