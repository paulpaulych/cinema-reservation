package dgis.interview.cinema

import java.io.InputStream
import java.io.InputStreamReader

object ResourceLoader{

    fun asText(name: String): String =
        InputStreamReader(asStream(name)).readText()

    //TODO: cache maybe?
    fun asStream(name: String): InputStream =
        this::class.java.classLoader.getResourceAsStream(name)
            ?: error("cannot find resource $name")

}