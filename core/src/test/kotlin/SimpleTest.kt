import notion.api.v1.NotionClient
import notion.api.v1.model.pages.Page
import notion.api.v1.model.pages.PageProperty
import notion.api.v1.request.pages.CreatePageRequest
import notion.api.v1.request.pages.UpdatePagePropertiesRequest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SimpleTest {

    @Test
    fun pages() {
        NotionClient(token = System.getenv("NOTION_TOKEN")).use { client ->
            val databases = client.listDatabases()
            assertTrue { databases.results.isNotEmpty() }

            val database = databases.results.find { it.title?.get(0)?.plainText == "Test Database" }!!

            // All the options for "Severity" property (select type)
            val severityOptions = database.properties?.get("Severity")?.select?.options
            // All the options for "Tags" property (multi_select type)
            val tagOptions = database.properties?.get("Tags")?.multiSelect?.options
            // The user object for "Assignee" property (people type)
            val assignee = client.listUsers().results[0] // just picking the first user up

            val newPage: Page = client.createPage(
                CreatePageRequest(
                    // Use the "Test Database" as this page's parent
                    parent = CreatePageRequest.Parent(type = "database", databaseId = database.id),
                    // Set values to the page's properties
                    // (these must be pre-defined before this API call)
                    properties = mapOf(
                        "Title" to prop(title = listOf(PageProperty.RichText(text = PageProperty.RichText.Text(content = "Fix a bug")))),
                        "Severity" to prop(select = severityOptions?.find { it.name == "High" }),
                        "Tags" to prop(multiSelect = tagOptions),
                        "Due" to prop(date = PageProperty.Date(start = "2021-05-13", end = "2021-12-31")),
                        "Velocity Points" to prop(number = 3),
                        "Assignee" to prop(people = listOf(assignee)),
                        "Done" to prop(checkbox = true),
                        "Link" to prop(url = "https://www.example.com"),
                        "Contact" to prop(email = "foo@example.com"),
                    )
                )
            )
            assertNotNull(newPage)

            val patchResult = client.updatePageProperties(
                UpdatePagePropertiesRequest(
                    pageId = newPage.id,
                    // Update only "Severity" property
                    properties = mapOf(
                        "Severity" to prop(select = severityOptions?.find { it.name == "Medium" }),
                    )
                )
            )
            assertNotEquals(newPage.lastEditedTime, patchResult.lastEditedTime)

            val fetchedPage = client.retrievePage(newPage.id)
            assertEquals(patchResult.lastEditedTime, fetchedPage.lastEditedTime)
        }
    }
}
