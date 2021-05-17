package notion.api.v1.endpoint

import notion.api.v1.exception.NotionAPIError
import notion.api.v1.http.NotionHttpClient
import notion.api.v1.json.NotionJsonSerializer
import notion.api.v1.logging.NotionLogger
import notion.api.v1.model.databases.Database
import notion.api.v1.model.databases.Databases
import notion.api.v1.model.databases.QueryResults
import notion.api.v1.model.databases.query.filter.QueryTopLevelFilter
import notion.api.v1.model.databases.query.sort.QuerySort
import notion.api.v1.request.databases.ListDatabasesRequest
import notion.api.v1.request.databases.QueryDatabaseRequest
import notion.api.v1.request.databases.RetrieveDatabaseRequest

interface DatabasesSupport : EndpointsSupport {
    val httpClient: NotionHttpClient
    val jsonSerializer: NotionJsonSerializer
    val logger: NotionLogger
    val baseUrl: String

    // -----------------------------------------------
    // listDatabases
    // -----------------------------------------------

    fun listDatabases(): Databases {
        return listDatabases(ListDatabasesRequest())
    }

    fun listDatabases(pageSize: Int, startCursor: String): Databases {
        return listDatabases(ListDatabasesRequest(startCursor = startCursor, pageSize = pageSize))
    }

    fun listDatabases(request: ListDatabasesRequest): Databases {
        val httpResponse =
            httpClient.get(
                logger = logger,
                url = "$baseUrl/databases",
                query = request.buildPaginationParams(),
                headers = buildRequestHeaders(emptyMap()))
        if (httpResponse.status == 200) {
            return jsonSerializer.toDatabases(httpResponse.body)
        } else {
            throw NotionAPIError(
                error = jsonSerializer.toError(httpResponse.body),
                httpResponse = httpResponse,
            )
        }
    }

    // -----------------------------------------------
    // retrieveDatabase
    // -----------------------------------------------

    fun retrieveDatabase(databaseId: String): Database {
        return retrieveDatabase(RetrieveDatabaseRequest(databaseId))
    }

    fun retrieveDatabase(request: RetrieveDatabaseRequest): Database {
        val httpResponse =
            httpClient.get(
                logger = logger,
                url = "$baseUrl/databases/${urlEncode(request.databaseId)}",
                headers = buildRequestHeaders(emptyMap()))
        if (httpResponse.status == 200) {
            return jsonSerializer.toDatabase(httpResponse.body)
        } else {
            throw NotionAPIError(
                error = jsonSerializer.toError(httpResponse.body),
                httpResponse = httpResponse,
            )
        }
    }

    // -----------------------------------------------
    // queryDatabase
    // -----------------------------------------------

    fun queryDatabase(
        databaseId: String,
        filter: QueryTopLevelFilter? = null,
        sorts: List<QuerySort>? = null,
        startCursor: String? = null,
        pageSize: Int? = null,
    ): QueryResults {
        return queryDatabase(
            QueryDatabaseRequest(
                databaseId = databaseId,
                filter = filter,
                sorts = sorts,
                startCursor = startCursor,
                pageSize = pageSize,
            ))
    }

    fun queryDatabase(request: QueryDatabaseRequest): QueryResults {
        val httpResponse =
            httpClient.postTextBody(
                logger = logger,
                url = "$baseUrl/databases/${urlEncode(request.databaseId)}/query",
                body = jsonSerializer.toJsonString(request),
                headers = buildRequestHeaders(contentTypeJson()))
        if (httpResponse.status == 200) {
            return jsonSerializer.toQueryResults(httpResponse.body)
        } else {
            throw NotionAPIError(
                error = jsonSerializer.toError(httpResponse.body),
                httpResponse = httpResponse,
            )
        }
    }
}
