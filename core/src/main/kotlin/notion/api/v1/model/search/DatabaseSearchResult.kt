package notion.api.v1.model.search

import com.google.gson.annotations.SerializedName
import notion.api.v1.model.common.Cover
import notion.api.v1.model.common.Icon
import notion.api.v1.model.common.ObjectType
import notion.api.v1.model.databases.DatabaseParent
import notion.api.v1.model.databases.DatabaseProperty

data class DatabaseSearchResult
@JvmOverloads
constructor(
    @SerializedName("object") override val objectType: ObjectType = ObjectType.Database,
    override val id: String,
    override val icon: Icon,
    override val cover: Cover,
    override val createdTime: String,
    override val lastEditedTime: String,
    val url: String,
    val parent: DatabaseParent,
    val title: List<DatabaseProperty.RichText>?,
    val properties: Map<String, DatabaseProperty>,
) : SearchResult
