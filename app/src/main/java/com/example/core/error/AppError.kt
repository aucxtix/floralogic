package com.example.core.error

/**
 * Standardized application error hierarchy.
 */
sealed class AppError(open val userMessage: String, open val cause: Throwable? = null) {
  data class Network(
    override val userMessage: String = "No internet connection. Using cached farm data.",
    override val cause: Throwable? = null
  ) : AppError(userMessage, cause)

  data class Authentication(
    override val userMessage: String = "Incorrect email or password.",
    override val cause: Throwable? = null
  ) : AppError(userMessage, cause)

  data class Validation(
    override val userMessage: String,
    val fieldName: String? = null
  ) : AppError(userMessage)

  data class Server(
    override val userMessage: String = "Server temporarily unavailable. Please try again later.",
    val statusCode: Int? = null,
    override val cause: Throwable? = null
  ) : AppError(userMessage, cause)

  data class Unknown(
    override val userMessage: String = "An unexpected error occurred. Please try again.",
    override val cause: Throwable? = null
  ) : AppError(userMessage, cause)
}
