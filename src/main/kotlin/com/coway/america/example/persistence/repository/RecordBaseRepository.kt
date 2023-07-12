package com.coway.america.example.persistence.repository

import org.jooq.Condition
import org.jooq.Table
import org.jooq.UpdatableRecord

interface RecordBaseRepository<R : UpdatableRecord<R>, T : Table<R>> {

    /**
     * 신규 레코드 저장
     *
     * @param recordDataSupplier 레코드 데이터 제공자
     * @return 신규로 저장된 레코드
     */
    fun create(createdBy: String, recordDataSupplier: (createRecord: R) -> Unit): R

    /**
     * 수정된 레코드 정보 저장
     *
     * @param updatedRecord 수정된 내용이 반영된 레코드
     * @param updatedBy 수정 사용자 계정 식별자
     * @return 수정된 사항이 저장된 레코드
     */
    fun update(updatedRecord: R, updatedBy: String): R

    /**
     * 레코드 삭제
     *
     * @param deleteRecord 삭제되는 레코드
     * @param deletedBy 삭제 사용자 계정 식별자
     * @return 삭제된 레코드
     */
    fun delete(deleteRecord: R, deletedBy: String): R

    /**
     * 레코드 단건 조회
     *
     * @param conditionSupplier 레코드 검색 조건 제공자
     * @return 조건에 부합되는 레코드
     */
    fun findOne(conditionSupplier: (table: T) -> List<Condition> = { emptyList() }): R?

    /**
     * 레코드 목록 조회
     *
     * @param conditionSupplier 레코드 검색 조건 제공자
     * @return 조건에 부합되는 레코드 목록
     */
    fun findList(conditionSupplier: (table: T) -> List<Condition> = { emptyList() }): List<R>
}
