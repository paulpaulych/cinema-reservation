package dgis.interview.cinema.reservation

import dgis.interview.cinema.session.Session
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class ReservationRepo(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {

    fun findBySession(session: Session): Set<Reservation> {
        TODO()
    }

    fun add(reservations: List<Reservation>){
        TODO()
    }

}
