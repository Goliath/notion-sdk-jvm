import notion.api.v1.NotionClient
import notion.api.v1.model.pages.PageProperty
import notion.api.v1.model.pages.PageProperty.Date
import notion.api.v1.model.pages.PageProperty.RichText
import notion.api.v1.request.pages.CreatePageRequest
import notion.api.v1.request.pages.UpdatePagePropertiesRequest

typealias prop = PageProperty

fun main() {
    val client = NotionClient(token = System.getenv("NOTION_TOKEN"))
    client.use {

        // Look up all databases that this app can access
        val databases = client.listDatabases()
        // Find the "Test Database" from the list
        val database = databases.results.find { it.title?.first()?.plainText == "Test Database" }
            ?: throw IllegalStateException("Create a database named 'Test Database' and invite this app's user!")

        // All the options for "Severity" property (select type)
        val severityOptions = database.properties?.get("Severity")?.select?.options
        // All the options for "Tags" property (multi_select type)
        val tagOptions = database.properties?.get("Tags")?.multiSelect?.options
        // The user object for "Assignee" property (people type)
        val assignee = client.listUsers().results[0] // just picking the first user up

        // Create a new page in the database
        val newPage = client.createPage(
            CreatePageRequest(
                // Use the "Test Database" as this page's parent
                parent = CreatePageRequest.Parent(type = "database", databaseId = database.id),
                // Set values to the page's properties
                // (these must be pre-defined before this API call)
                properties = mapOf(
                    "Title" to prop(title = listOf(RichText(text = RichText.Text(content = "Fix a bug")))),
                    "Severity" to prop(select = severityOptions?.find { it.name == "High" }),
                    "Tags" to prop(multiSelect = tagOptions),
                    "Due" to prop(date = Date(start = "2021-05-13", end = "2021-12-31")),
                    "Velocity Points" to prop(number = 3),
                    "Assignee" to prop(people = listOf(assignee)),
                    "Done" to prop(checkbox = true),
                    "Link" to prop(url = "https://www.example.com"),
                    "Contact" to prop(email = "foo@example.com"),
                )
            )
        )

        // Update properties in the page
        val updatedPage =
            client.updatePageProperties(
                UpdatePagePropertiesRequest(
                    pageId = newPage.id,
                    // Update only "Severity" property
                    properties = mapOf(
                        "Severity" to prop(select = severityOptions?.find { it.name == "Medium" }),
                    )
                )
            )

        // Fetch the latest data of the page
        val retrievedPage = client.retrievePage(newPage.id)
    }
}
