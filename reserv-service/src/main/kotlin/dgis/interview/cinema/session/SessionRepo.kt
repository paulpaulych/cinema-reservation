package dgis.interview.cinema.session

import dgis.interview.cinema.IdAccessRepo
import org.springframework.stereotype.Repository

@Repository
class SessionRepo: IdAccessRepo<Session, Long> {

    override fun findById(id: Long): Session? {
        TODO()
    }

    fun add(session: Session) {
        TODO()
    }
}

