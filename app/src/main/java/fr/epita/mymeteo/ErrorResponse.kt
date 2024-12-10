package fr.epita.mymeteo

data class ErrorResponse(
    val error: ErrorDetail
)

data class ErrorDetail(
    val code: Int,
    val message: String
)
