package notion.api.v1.model.users

import com.google.gson.annotations.SerializedName
import notion.api.v1.model.common.ObjectType
import notion.api.v1.model.common.Pagination

data class Users(
    @SerializedName("object") override val objectType: String = "list",
    val results: List<User>,
    override val nextCursor: String? = null,
    override val hasMore: Boolean = false,
) : ObjectType, Pagination
