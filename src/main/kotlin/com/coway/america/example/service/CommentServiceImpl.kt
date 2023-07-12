package com.coway.america.example.service

import com.coway.america.example.model.comment.CommentCreateItem
import com.coway.america.example.model.comment.CommentDetailItem
import com.coway.america.example.model.comment.CommentListItem
import com.coway.america.example.model.comment.CommentUpdateItem
import com.coway.america.example.persistence.repository.CommentRepository
import com.coway.america.persistence.tables.records.CommentRecord
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.text.RandomStringGenerator
import org.springframework.stereotype.Service
import java.time.ZoneId

@Service
class CommentServiceImpl(
    private val commentRepository: CommentRepository
) : CommentService {

    override fun getCommentList(): List<CommentListItem> {
        return commentRepository.findList()
            .map { commentRecord ->
                CommentListItem(
                    id = commentRecord.commentId!!,
                    contents = commentRecord.contents!!,
                    viewCount = commentRecord.viewCount!!
                )
            }
    }

    override fun createComment(createItem: CommentCreateItem, createdBy: String): CommentDetailItem {
        return commentRepository.create(createdBy) { commentRecord ->
            commentRecord.commentId = RandomStringUtils.randomAlphabetic(16)
            commentRecord.contents = createItem.contents
        }.let { createdRecord ->
            CommentDetailItem(
                id = createdRecord.commentId!!,
                contents = createdRecord.contents!!,
                viewCount = createdRecord.viewCount ?: 0,
                createdDateTime = createdRecord.createdAt!!.atZone(ZoneId.systemDefault())
            )
        }
    }

    override fun getCommentDetail(commentId: String, viewerBy: String): CommentDetailItem? {
        return commentRepository.findOne { commentTable ->
            listOf(
                commentTable.COMMENT_ID.eq(commentId)
            )
        }?.let { commentRecord ->
            var viewCount = commentRecord.viewCount ?: 0
            if (commentRecord.createdBy != viewerBy) {
                viewCount += 1
            }
            commentRecord.viewCount = viewCount
            commentRepository.update(commentRecord, viewerBy)

            CommentDetailItem(
                id = commentRecord.commentId!!,
                contents = commentRecord.contents!!,
                viewCount = viewCount,
                createdDateTime = commentRecord.createdAt!!.atZone(ZoneId.systemDefault())
            )
        }
    }

    override fun updateCommentDetail(
        commentId: String,
        updateItem: CommentUpdateItem,
        updatedBy: String
    ): CommentDetailItem {
        return commentRepository.findOne { commentTable ->
            listOf(
                commentTable.COMMENT_ID.eq(commentId)
            )
        }!!.let { commentRecord ->
            commentRecord.contents = updateItem.contents
            val updatedCommentRecord = commentRepository.update(commentRecord, updatedBy)

            CommentDetailItem(
                id = updatedCommentRecord.commentId!!,
                contents = updatedCommentRecord.contents!!,
                viewCount = updatedCommentRecord.viewCount ?: 0,
                createdDateTime = updatedCommentRecord.createdAt!!.atZone(ZoneId.systemDefault())
            )
        }
    }

    override fun deleteComment(commentId: String, deletedBy: String) {
        val commentRecord = commentRepository.findOne { commentTable ->
            listOf(
                commentTable.COMMENT_ID.eq(commentId)
            )
        }

        if (commentRecord != null) {
            commentRepository.delete(commentRecord, deletedBy)
        }
    }
}
