package com.coway.america.example.persistence.repository

import com.coway.america.persistence.tables.Comment
import com.coway.america.persistence.tables.records.CommentRecord

interface CommentRepository : RecordBaseRepository<CommentRecord, Comment>
