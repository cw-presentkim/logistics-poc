package com.coway.america.example.exception

/**
 * Jooq Record Field 관련 오류
 */
class RecordFieldException(override val message: String) : RuntimeException(message)
