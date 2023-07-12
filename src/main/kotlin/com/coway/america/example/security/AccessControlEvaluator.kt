package com.coway.america.example.security

interface AccessControlEvaluator {

    /**
     * 권한이 있는지 확인
     *
     * @param resourceId 리소스 식별자
     * @param actionId 리소스 동작 식별자
     * @return 권한 보유 여부
     */
    fun hasPermission(resourceId: String, actionId: String): Boolean
}
