package dgis.interview.cinema

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Позволяет объявлять логгер в виде:
 * private val log by LoggerProperty()
 * Это короче, чем явное использование LoggerFactory
 * Меньше зависимостей, чем при использовании аннотации Ломбока
 */
class LoggerProperty : ReadOnlyProperty<Any?, Logger> {

    companion object {
        private fun <T> createLogger(clazz: Class<T>) =
            LoggerFactory.getLogger(clazz)
    }

    private var logger: Logger? = null

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Logger {
        if (logger == null) {
            logger = createLogger(thisRef!!.javaClass)
        }
        return logger!!
    }
}