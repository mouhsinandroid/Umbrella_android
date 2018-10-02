package org.secfirst.umbrella.whitelabel.data.database.segment

import android.os.Parcelable
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.BaseModel
import org.secfirst.umbrella.whitelabel.data.database.content.Module
import org.secfirst.umbrella.whitelabel.data.database.content.Subject
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentController
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentDetailController


@Parcelize
data class Segment(var toolbarTitle: String,
                   var tabTitle: String,
                   var markdowns: List<Markdown> = listOf()) : Parcelable

@Parcelize
@Table(database = AppDatabase::class, allFields = true)
data class Markdown(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,

        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "category_id")
        @ForeignKey(stubbedRelationship = true)
        var module: Module? = null,

        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "subcategory_id")
        @ForeignKey(stubbedRelationship = true)
        var subject: Subject? = null,

        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "child_id")
        @ForeignKey(stubbedRelationship = true)
        var difficulty: Difficulty? = null,
        var text: String = "",
        var title: String = "",
        var index: String = "") : BaseModel(), Parcelable {

    constructor(text: String) : this(0,
            null,
            null,
            null, text, recoveryTitle(text), recoveryIndex(text))

    companion object {
        const val TAG_INDEX = "index: "
        const val TAG_TITLE = "title: "
        const val SINGLE_CHOICE = 1
        fun recoveryIndex(text: String) = text.lines()[1].trim().substringAfterLast(TAG_INDEX)
        fun recoveryTitle(text: String) = text.lines()[2].trim().substringAfterLast(TAG_TITLE)
    }
}

fun MutableList<Markdown>.toSegment(toolbarTitle: String, title: String): Segment {
    val markdowns = mutableListOf<Markdown>()
    this.forEach { markdown ->
        markdowns.add(markdown)
    }
    return Segment(toolbarTitle, title, markdowns)
}


fun Segment.toController() = SegmentController(this.markdowns, this.tabTitle)

fun List<Markdown>.toControllers(): List<SegmentDetailController> {
    val controllers = mutableListOf<SegmentDetailController>()
    this.forEach { markdown ->
        val markdowns = mutableListOf<Markdown>()
        markdowns.add(markdown)
        val controller = SegmentDetailController(markdown)
        controllers.add(controller)
    }
    return controllers
}

fun Markdown.removeHead(): Markdown {
    for ((cont, line) in this.text.lines().withIndex()) {
        if (cont < 3) {
            if (line.contains(Markdown.TAG_TITLE)) {
                this.title = line.trim().substringAfterLast(Markdown.TAG_TITLE)
                break
            } else if (line.contains(Markdown.TAG_INDEX)) {
                this.index = line.trim().substringAfterLast(Markdown.TAG_INDEX)
                break
            }

        }

    }
    //this.text = this.text.substringAfterLast(this.text.lines()[0]) + "#"
    return this
}

interface HostSegmentTabControl {

    fun onTabHostManager(position: Int)
}