package notion.api.v1.model.blocks

import com.google.gson.annotations.SerializedName
import java.util.*
import notion.api.v1.model.common.ObjectType
import notion.api.v1.model.pages.PageProperty

open class BulletedListItemBlock
@JvmOverloads
constructor(
    @SerializedName("object") override val objectType: ObjectType = ObjectType.Block,
    override val type: BlockType = BlockType.BulletedListItem,
    override var id: String? = UUID.randomUUID().toString(),
    override var createdTime: String? = null,
    override var lastEditedTime: String? = null,
    override var hasChildren: Boolean? = null,
    override var archived: Boolean? = null,
    val bulletedListItem: Element,
) : Block {

  // for other JVM languages
  constructor(
      bulletedListItem: Element,
      id: String? = UUID.randomUUID().toString(),
      hasChildren: Boolean? = null,
      createdTime: String? = null,
      lastEditedTime: String? = null,
  ) : this(
      objectType = ObjectType.Block,
      type = BlockType.BulletedListItem,
      id = id,
      createdTime = createdTime,
      lastEditedTime = lastEditedTime,
      hasChildren = hasChildren,
      bulletedListItem = bulletedListItem)

  open class Element
  @JvmOverloads
  constructor(var text: List<PageProperty.RichText>, var children: List<Block>? = null)
}
