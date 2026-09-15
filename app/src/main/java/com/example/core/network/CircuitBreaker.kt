package com.example.core.network

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withTimeout
import java.util.concurrent.atomic.AtomicInteger

/**
 * Resilient Circuit Breaker and Concurrency Limiter for external dependencies (e.g. Open-Meteo REST API).
 * Prevents cascading thread/connection pool exhaustion when downstream services degrade or hang.
 *
 * States:
 * - CLOSED: Normal operation. Requests flow through subject to concurrency limits and timeouts.
 * - OPEN: Downstream is failing/slow. Requests fast-fail immediately to fallback without network roundtrips.
 * - HALF_OPEN: Cooldown period elapsed. A limited canary request probes if the dependency has recovered.
 */
class CircuitBreaker(
  val name: String = "ExternalDependency",
  val failureThreshold: Int = 3,
  val cooldownDurationMs: Long = 15_000L,
  val timeoutMs: Long = 6_000L,
  val maxConcurrentRequests: Int = 2
) {
  enum class State {
    CLOSED,
    OPEN,
    HALF_OPEN
  }

  class CircuitBreakerOpenException(message: String) : Exception(message)

  private val mutex = Mutex()
  private val semaphore = Semaphore(permits = maxConcurrentRequests)

  @Volatile
  var state: State = State.CLOSED
    private set

  private val consecutiveFailures = AtomicInteger(0)
  private val totalTrips = AtomicInteger(0)

  @Volatile
  var lastTripTimestamp: Long = 0L
    private set

  val currentConsecutiveFailures: Int get() = consecutiveFailures.get()
  val currentTotalTrips: Int get() = totalTrips.get()

  /**
   * Executes a protected external call.
   * If the circuit is OPEN, fast-fails immediately to [fallback] without making remote requests.
   * Restricts concurrency using [semaphore] and caps execution time with [timeoutMs].
   */
  suspend fun <T> execute(
    fallback: suspend (Throwable?) -> T,
    block: suspend () -> T
  ): T {
    // 1. Check Circuit State before acquiring network resources
    val currentState = checkAndTransitionState()
    if (currentState == State.OPEN) {
      val timeRemaining = cooldownDurationMs - (System.currentTimeMillis() - lastTripTimestamp)
      return fallback(
        CircuitBreakerOpenException(
          "Circuit '$name' is OPEN. Fast-failing downstream request. Cooldown remaining: ${timeRemaining.coerceAtLeast(0)}ms"
        )
      )
    }

    // 2. Concurrency limiting to prevent thread/socket exhaustion
    return try {
      semaphore.withPermit {
        withTimeout(timeoutMs) {
          val result = block()
          onSuccess()
          result
        }
      }
    } catch (e: Throwable) {
      onFailure(e)
      fallback(e)
    }
  }

  private suspend fun checkAndTransitionState(): State = mutex.withLock {
    val now = System.currentTimeMillis()
    if (state == State.OPEN && (now - lastTripTimestamp) >= cooldownDurationMs) {
      state = State.HALF_OPEN
    }
    state
  }

  private suspend fun onSuccess() = mutex.withLock {
    consecutiveFailures.set(0)
    if (state == State.HALF_OPEN) {
      state = State.CLOSED
    }
  }

  private suspend fun onFailure(throwable: Throwable) = mutex.withLock {
    val failures = consecutiveFailures.incrementAndGet()
    if (state == State.HALF_OPEN || failures >= failureThreshold) {
      tripCircuit()
    }
  }

  suspend fun tripCircuit() = mutex.withLock {
    state = State.OPEN
    lastTripTimestamp = System.currentTimeMillis()
    totalTrips.incrementAndGet()
  }

  suspend fun reset() = mutex.withLock {
    state = State.CLOSED
    consecutiveFailures.set(0)
    lastTripTimestamp = 0L
  }
}
