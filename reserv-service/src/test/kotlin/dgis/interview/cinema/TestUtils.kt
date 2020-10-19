package dgis.interview.cinema

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.fasterxml.jackson.module.kotlin.readValue
import io.restassured.response.ValidatableResponse

inline fun <reified T> ValidatableResponse.extractBody(objectMapper: ObjectMapper) =
    objectMapper.readValue<T>(this.extract().body().asString())

internal inline fun <reified T> SafeErrorRes.readPayload(objectMapper: ObjectMapper) =
        objectMapper.convertValue(this.payload, jacksonTypeRef<T>())

internal data class SafeErrorRes(
        val code: String,
        val message: String?,
        val payload: JsonNode?
)