package notion.api.v1.model.blocks

import com.google.gson.annotations.SerializedName
import java.util.*
import notion.api.v1.model.common.ObjectType
import notion.api.v1.model.pages.PageProperty

open class ParagraphBlock
@JvmOverloads
constructor(
    @SerializedName("object") override val objectType: ObjectType = ObjectType.Block,
    override var type: BlockType = BlockType.Paragraph,
    override var id: String? = UUID.randomUUID().toString(),
    override var createdTime: String? = null,
    override var lastEditedTime: String? = null,
    override var hasChildren: Boolean? = null,
    override var archived: Boolean? = null,
    val paragraph: Element,
) : Block {

  // for other JVM languages
  constructor(
      paragraph: Element,
      id: String? = UUID.randomUUID().toString(),
      hasChildren: Boolean? = null,
      createdTime: String? = null,
      lastEditedTime: String? = null,
  ) : this(
      objectType = ObjectType.Block,
      type = BlockType.Paragraph,
      id = id,
      createdTime = createdTime,
      lastEditedTime = lastEditedTime,
      hasChildren = hasChildren,
      paragraph = paragraph)

  open class Element
  @JvmOverloads
  constructor(var text: List<PageProperty.RichText>, var children: List<Block>? = null)
}
