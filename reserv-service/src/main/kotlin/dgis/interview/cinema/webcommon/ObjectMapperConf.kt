package dgis.interview.cinema.webcommon

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ObjectMapperConf {

    @Bean
    fun objectMapper(): ObjectMapper =
        jacksonObjectMapper().apply {
            deserializationConfig.apply {
                withFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            }
        }
}