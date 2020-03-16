package com.pj.playground.utils

import com.google.gson.Gson
import okhttp3.*

private val DUMMY_RESULTS = listOf(
    "Hello, coroutines!",
    "My favorite feature",
    "Async made easy",
    "Coroutines by example",
    "Check out the Advanced Coroutines codelab next!"
)

/**
 * This class will return dummy [Response] objects to Retrofit, without actually using the network.
 */
class NetworkInterceptor : Interceptor {
    private var lastResult: String = ""
    private val gson = Gson()

    private var attempts = 0

    /**
     * Return true iff this request should error.
     */
    private fun wantRandomError() = attempts++ % 5 == 0

    /**
     * Stop the request from actually going out to the network.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        pretendToBlockForNetworkRequest()
        return if (wantRandomError()) {
            makeErrorResult(chain.request())
        } else {
            makeOkResult(chain.request())
        }
    }

    /**
     * Generate a success response.
     *
     * ```
     * HTTP/1.1 200 OK
     * Content-type: application/json
     *
     * "$random_string"
     * ```
     */
    private fun makeOkResult(request: Request): Response {
        var nextResult = lastResult
        while (nextResult == lastResult) {
            nextResult = DUMMY_RESULTS.random()
        }
        lastResult = nextResult

        return Response.Builder()
            .code(200)
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .message("OK")
            .body(
                ResponseBody.create(
                    MediaType.get("application/json"), gson.toJson(nextResult)
                )
            )
            .build()
    }

    /**
     * Generate an error result.
     *
     * ```
     * HTTP/1.1 500 Bad server day
     * Content-type: application/json
     *
     * {"cause": "not sure"}
     * ```
     */
    private fun makeErrorResult(request: Request) = Response.Builder()
        .code(500)
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .message("Bad server day")
        .body(
            ResponseBody.create(
                MediaType.get("application/json"),
                gson.toJson(mapOf("cause" to "not sure"))
            )
        )
        .build()

    /**
     * Pretend to "block" interacting with the network.
     *
     * Really: sleep for 500ms.
     */
    private fun pretendToBlockForNetworkRequest() = Thread.sleep(500)
}