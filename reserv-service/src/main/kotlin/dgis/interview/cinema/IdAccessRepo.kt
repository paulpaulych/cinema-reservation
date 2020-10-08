package dgis.interview.cinema

interface IdAccessRepo<T, ID>{
    fun findById(id: ID): T?
}

sealed class AddOneRes {
    object Success: AddOneRes()
    object AlreadyExists: AddOneRes()
}