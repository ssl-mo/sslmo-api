import kotlinx.serialization.Serializable

@Serializable
data class DefaultResponse(val message: String)


@Serializable
sealed class Response {


    @Serializable
    data class Success<T>(
        val data: T,
        val message: String,
    ) : Response()

    @Serializable
    data class Error<T>(
        val error: T,
        val message: String,
    ) : Response()
}