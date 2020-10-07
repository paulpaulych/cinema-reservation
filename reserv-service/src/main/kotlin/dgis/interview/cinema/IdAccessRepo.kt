package dgis.interview.cinema

interface IdAccessRepo<T, ID>{
    fun findById(id: ID): T?
}