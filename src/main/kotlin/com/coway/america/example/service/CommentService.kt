package com.coway.america.example.service

import com.coway.america.example.model.comment.CommentCreateItem
import com.coway.america.example.model.comment.CommentDetailItem
import com.coway.america.example.model.comment.CommentListItem
import com.coway.america.example.model.comment.CommentUpdateItem

interface CommentService {

    /**
     * 댓글 목록 조회
     */
    fun getCommentList(): List<CommentListItem>

    /**
     * 댓글 생성
     *
     * @param createItem 생성 댓글 내용
     * @param createdBy 댓글 생성 사용자
     * @return 댓글 상세
     */
    fun createComment(createItem: CommentCreateItem, createdBy: String): CommentDetailItem

    /**
     * 댓글 상세 조회
     *
     * @param commentId 댓글 식별자
     * @param viewerBy 댓글 조회 사용자
     * @return 댓글 상세
     */
    fun getCommentDetail(commentId: String, viewerBy: String): CommentDetailItem?

    /**
     * 댓글 수정
     *
     * @param commentId 댓글 식별자
     * @param updateItem 댓글 수정 정보
     * @param updatedBy 댓글 수정 사용자
     * @return 수정된 댓글 상세
     */
    fun updateCommentDetail(commentId: String, updateItem: CommentUpdateItem, updatedBy: String): CommentDetailItem

    /**
     * 댓글 삭제
     *
     * @param commentId 댓글 식별자
     * @param deletedBy 댓글 삭제 사용자
     */
    fun deleteComment(commentId: String, deletedBy: String)
}
