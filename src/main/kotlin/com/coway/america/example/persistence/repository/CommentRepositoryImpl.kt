package com.coway.america.example.persistence.repository

import com.coway.america.persistence.tables.Comment
import com.coway.america.persistence.tables.records.CommentAuditRecord
import com.coway.america.persistence.tables.records.CommentRecord
import com.coway.america.persistence.tables.references.COMMENT_AUDIT
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class CommentRepositoryImpl(
    dslContext: DSLContext
) : CommentRepository, RecordBaseRepositoryAdaptor<CommentRecord, Comment, CommentAuditRecord>(
    dslContext,
    tableRecord = Comment.COMMENT,
    auditTable = COMMENT_AUDIT
)
