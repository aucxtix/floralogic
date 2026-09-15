package com.example.core.network

/**
 * Standard NetworkResult wrapper representing asynchronous operations.
 */
sealed class NetworkResult<out T> {
  data class Success<out T>(val data: T) : NetworkResult<T>()
  data class Error(
    val message: String,
    val statusCode: Int? = null,
    val throwable: Throwable? = null
  ) : NetworkResult<Nothing>()
  object Loading : NetworkResult<Nothing>()

  val isSuccess: Boolean get() = this is Success
  val isError: Boolean get() = this is Error
  val isLoading: Boolean get() = this is Loading

  fun getOrNull(): T? = (this as? Success)?.data
}
