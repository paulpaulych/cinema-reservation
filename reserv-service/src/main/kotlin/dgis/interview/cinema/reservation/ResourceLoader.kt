package dgis.interview.cinema.reservation

import java.io.InputStream
import java.io.InputStreamReader

class ResourceLoader{

    companion object {
        fun asText(name: String): String =
            InputStreamReader(asStream(name)).readText()

        fun asStream(name: String): InputStream =
            this::class.java.classLoader.getResourceAsStream(name)
                ?: error("cannot find resource $name")
    }
}