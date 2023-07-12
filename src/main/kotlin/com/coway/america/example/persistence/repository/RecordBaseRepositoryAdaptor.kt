package com.coway.america.example.persistence.repository

import com.coway.america.example.exception.RecordFieldException
import org.jooq.*
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

abstract class RecordBaseRepositoryAdaptor<R : UpdatableRecord<R>, T : Table<R>, A : UpdatableRecord<A>>(
    protected val dslContext: DSLContext,
    private val tableRecord: T,
    private val auditTable: Table<A>,
    createdAtFieldName: String = "created_at",
    createdByFieldName: String = "created_by",
    updatedAtFieldName: String = "updated_at",
    updatedByFieldName: String = "updated_by",
    deletedFieldName: String = "deleted",
    deletedUniqueAvoidFieldName: String = "deleted_unique_avoid"
) : RecordBaseRepository<R, T> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val deletedField = tableRecord.field(deletedFieldName, Boolean::class.java)

    private val createdAtField = tableRecord.field(createdAtFieldName, LocalDateTime::class.java)

    private val createdByField = tableRecord.field(createdByFieldName, String::class.java)

    private val updatedAtField = tableRecord.field(updatedAtFieldName, LocalDateTime::class.java)

    private val updatedByField = tableRecord.field(updatedByFieldName, String::class.java)

    private val deletedUniqueAvoidField = tableRecord.field(deletedUniqueAvoidFieldName, Int::class.java)

    override fun create(createdBy: String, recordDataSupplier: (createRecord: R) -> Unit): R {
        val createRecord = dslContext.newRecord(tableRecord)
        recordDataSupplier(createRecord)

        val createdAtLocalDataTime = LocalDateTime.now()
        if (createdAtField != null) {
            createRecord[createdAtField] = createdAtLocalDataTime
        }
        if (createdByField != null) {
            createRecord[createdByField] = createdBy
        }
        if (updatedAtField != null) {
            createRecord[updatedAtField] = createdAtLocalDataTime
        }
        if (updatedByField != null) {
            createRecord[updatedByField] = createdBy
        }

        if (deletedUniqueAvoidField != null) {
            createRecord[deletedUniqueAvoidField] = 0
        }

        createRecord.store()

        return createRecord
    }

    @Transactional
    override fun update(updatedRecord: R, updatedBy: String): R {
        return storeIfUpdated(updatedRecord, updatedBy)
    }

    @Transactional
    override fun delete(deleteRecord: R, deletedBy: String): R {
        if (deletedField == null) {
            throw RecordFieldException(
                "The record could not be deleted because the mark as deleted field does not exist."
            )
        }

        deleteRecord[deletedField] = true

        return storeIfUpdated(deleteRecord, deletedBy) { storeRecord ->
            if (deletedUniqueAvoidField != null) {
                val maxDeletedUniqueExcludeField = dslContext
                    .select(
                        DSL.max(deletedUniqueAvoidField)
                    )
                    .from(tableRecord)
                    .fetchOneInto(Int::class.java)
                    ?: 0

                deleteRecord[deletedUniqueAvoidField] = maxDeletedUniqueExcludeField + 1
            }
        }
    }

    override fun findOne(conditionSupplier: (table: T) -> List<Condition>): R? {
        val conditionList = mutableListOf<Condition>()
        conditionList.addAll(conditionSupplier(tableRecord))

        if (deletedField != null) {
            conditionList.add(
                deletedField.isFalse
            )
        }

        return dslContext.selectFrom(tableRecord)
            .where(conditionList)
            .fetchOneInto(tableRecord)
    }

    override fun findList(conditionSupplier: (table: T) -> List<Condition>): List<R> {
        val conditionList = mutableListOf<Condition>()
        conditionList.addAll(conditionSupplier(tableRecord))

        if (deletedField != null) {
            conditionList.add(
                deletedField.isFalse
            )
        }

        return dslContext.selectFrom(tableRecord)
            .where(conditionList)
            .fetchInto(tableRecord)
    }

    private fun storeIfUpdated(
        updatedRecord: R,
        updatedBy: String,
        storeRecordCustomizer: (storeRecord: R) -> Unit = {}
    ): R {
        val beforeUpdate = updatedRecord.original()

        // compareTo 로 Dirty Check 수행
        val modified = updatedRecord.changed() && updatedRecord.compareTo(beforeUpdate) != 0
        if (modified) {
            updatedRecord.configuration()?.dsl()
            val auditRecord = dslContext.newRecord(auditTable)
            auditRecord.fromMap(beforeUpdate.intoMap())
            dslContext.executeUpdate(auditRecord)
            auditRecord.store()

            storeRecordCustomizer(updatedRecord)

            updatedRecord[updatedAtField] = LocalDateTime.now()
            updatedRecord[updatedByField] = updatedBy
            updatedRecord.store()
        } else {
            logger.warn("No changes, No store. [${updatedRecord.intoMap()}]")
        }

        return updatedRecord
    }
}
