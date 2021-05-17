package tests.endpoint

import kotlin.test.assertNotNull
import notion.api.v1.NotionClient
import org.junit.Test

class SearchTest {

    @Test
    fun search() {
        NotionClient(token = System.getenv("NOTION_TOKEN")).use { client ->
            val searchResult = client.search("Great example data")
            assertNotNull(searchResult.results)
        }
    }
}
