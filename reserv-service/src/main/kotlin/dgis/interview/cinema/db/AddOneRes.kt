package dgis.interview.cinema.db

/**
 * default return value for add-one operations
 */
sealed class AddOneRes {
    object Success: AddOneRes()
    object AlreadyExists: AddOneRes()
}