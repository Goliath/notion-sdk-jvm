package notion.api.v1.http

import notion.api.v1.http.UrlConnPatchMethodWorkaround.setPatchRequestMethod
import notion.api.v1.logging.NotionLogger
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class UrlConnNotionHttpClient(
    private val connectTimeoutMillis: Int = 1_000,
    private val readTimeoutMillis: Int = 10_000,
) : NotionHttpClient {

    override fun get(
        logger: NotionLogger,
        url: String,
        query: Map<String, String>,
        headers: Map<String, String>
    ): NotionHttpResponse {
        val q = buildQueryString(query)
        val fullUrl = buildFullUrl(url, q)
        val conn = buildConnectionObject(fullUrl, headers)
        try {
            conn.requestMethod = "GET"
            debugLogStart(logger, conn, fullUrl)
            connect(conn).use { input ->
                val response = NotionHttpResponse(
                    status = conn.responseCode,
                    body = readResponseBody(input),
                    headers = conn.headerFields
                )
                debugLogSuccess(logger, response)
                return response
            }
        } finally {
            disconnect(conn, logger)
        }
    }

    override fun postTextBody(
        logger: NotionLogger,
        url: String,
        query: Map<String, String>,
        body: String,
        headers: Map<String, String>
    ): NotionHttpResponse {
        val q = buildQueryString(query)
        val fullUrl = buildFullUrl(url, q)
        val conn = buildConnectionObject(fullUrl, headers)
        try {
            conn.requestMethod = "POST"
            debugLogStart(logger, conn, fullUrl)
            setRequestBody(conn, body)
            connect(conn).use { input ->
                val response = NotionHttpResponse(
                    status = conn.responseCode,
                    body = readResponseBody(input),
                    headers = conn.headerFields
                )
                debugLogSuccess(logger, response)
                return response
            }
        } finally {
            disconnect(conn, logger)
        }
    }

    override fun patchTextBody(
        logger: NotionLogger,
        url: String,
        query: Map<String, String>,
        body: String,
        headers: Map<String, String>
    ): NotionHttpResponse {
        val q = buildQueryString(query)
        val fullUrl = buildFullUrl(url, q)
        val conn = buildConnectionObject(fullUrl, headers)
        try {
            setPatchRequestMethod(conn)
            debugLogStart(logger, conn, fullUrl)
            setRequestBody(conn, body)
            connect(conn).use { input ->
                val response = NotionHttpResponse(
                    status = conn.responseCode,
                    body = readResponseBody(input),
                    headers = conn.headerFields
                )
                debugLogSuccess(logger, response)
                return response
            }
        } finally {
            disconnect(conn, logger)
        }
    }

    // -----------------------------------------------

    private fun buildConnectionObject(fullUrl: String, headers: Map<String, String>): HttpURLConnection {
        val conn = URL(fullUrl).openConnection() as HttpURLConnection
        conn.setRequestProperty("Connection", "close")
        conn.connectTimeout = connectTimeoutMillis
        conn.readTimeout = readTimeoutMillis
        headers.forEach { (name, value) -> conn.setRequestProperty(name, value) }
        return conn
    }

    private fun setRequestBody(conn: HttpURLConnection, body: String) {
        conn.doOutput = true
        conn.outputStream.use { out -> out.write(body.toByteArray(Charsets.UTF_8)) }
    }

    private fun connect(conn: HttpURLConnection): InputStream = try {
        conn.connect()
        conn.inputStream
    } catch (e: IOException) {
        conn.errorStream
    }

    private fun readResponseBody(input: InputStream?): String {
        return input?.bufferedReader(Charsets.UTF_8).use { it?.readText() } ?: "";
    }

    private fun disconnect(conn: HttpURLConnection, logger: NotionLogger) {
        try {
            conn.disconnect()
        } catch (e: Exception) {
            debugLogFailure(logger, e)
        }
    }

    private fun debugLogStart(
        logger: NotionLogger,
        conn: HttpURLConnection,
        fullUrl: String
    ) {
        logger.debug("Sending a request - ${conn.requestMethod} $fullUrl")
    }

    private fun debugLogFailure(logger: NotionLogger, e: Exception) {
        logger.info("Failed to disconnect from Notion: ${e.message}", e)
    }

    private fun debugLogSuccess(
        logger: NotionLogger,
        response: NotionHttpResponse
    ) {
        logger.debug("Received a response (status: ${response.status}, body: ${response.body})")
    }

}