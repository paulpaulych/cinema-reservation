package dgis.interview.cinema.db

interface IdAccessRepo<T, ID>{
    fun findById(id: ID): T?
}

