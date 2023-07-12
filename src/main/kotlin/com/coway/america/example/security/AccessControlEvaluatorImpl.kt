package com.coway.america.example.security

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component("accessControl")
class AccessControlEvaluatorImpl(
    @Value("\${coway-sdk.security.api-key}")
    private val apiKey: String,
    @Value("\${coway-sdk.security.debug:false}")
    private val enableDebug: Boolean
) : AccessControlEvaluator, Logging {

    companion object {
        /**
         * 원격 호출 URL.
         */
        const val REMOTE_URL = "https://api.cw-sdk.coway.do/acl/resource/action/allowed"

        /**
         * 원격 호출시 API 인증 키 Header 이름.
         */
        const val REMOTE_API_KEY_HEADER = "X-ApiKey"
    }

    // Json body 를 위한 object mapper 생성
    private val objectMapper = jacksonObjectMapper()

    // HTTP 실행을 위한 OkHttpClient 초기화. 디버그가 활성화 된 경우 Body 를 로그에 기록.
    private val okHttpClient = OkHttpClient().newBuilder()
        .let { clientBuilder ->
            if (enableDebug) {
                clientBuilder.addInterceptor(
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                )
            }

            clientBuilder.addInterceptor { chain ->
                val addApiKeyHeaderRequest = chain.request()
                    .newBuilder()
                    .addHeader(REMOTE_API_KEY_HEADER, apiKey)
                    .build()

                chain.proceed(addApiKeyHeaderRequest)
            }
        }
        .build()

    /**
     * 사용자 인증이 된 상태인지 확인 후 인증 상태일 경우 주어진 권한 확인 코드 수행
     *
     * @param resourceId 리소스 식별자
     * @param actionId 리소스 동작 식별자
     * @return 권한 보유 여부
     */
    override fun hasPermission(resourceId: String, actionId: String): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication == null || !authentication.isAuthenticated) {
            false
        } else {
            remotePermissionEvaluator(authentication.name, resourceId, actionId)
        }
    }

    private fun remotePermissionEvaluator(userId: String, resourceId: String, actionId: String): Boolean {
        val requestBody = objectMapper
            .writeValueAsString(
                BodyPayload(userId, resourceId, actionId)
            )
            .toRequestBody(MediaType.APPLICATION_JSON_VALUE.toMediaType())

        val request = Request.Builder()
            .url(REMOTE_URL)
            .post(requestBody)
            .build()

        val response = okHttpClient.newCall(request).execute()

        if (response.code == HttpStatus.OK.value()) {
            return response.body
                ?.string()
                ?.let { objectMapper.readValue(it, ResponseBody::class.java) }
                ?.data
                ?.allowed
                ?: false
        } else {
            logger.warn {
                "Remote permission evaluator response is not ok. [${response.code}]"
            }
        }

        return false
    }

    /**
     * 원격지에 전송할 Body 의 모델
     */
    private data class BodyPayload(
        @JsonProperty("userId")
        val userId: String,
        @JsonProperty("resourceId")
        val resourceId: String,
        @JsonProperty("actionId")
        val actionId: String
    )

    /**
     * 원격지에서 응답될 Body 의 모델
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class ResponseBody(
        @JsonProperty("code")
        val code: String,
        @JsonProperty("message")
        val message: String = "",
        @JsonProperty("data")
        val data: ResponseBodyData
    )

    /**
     * 원격지에서 응답될 Body 의 data 모델
     */
    private data class ResponseBodyData(
        @JsonProperty("allowed")
        val allowed: Boolean = false
    )
}
