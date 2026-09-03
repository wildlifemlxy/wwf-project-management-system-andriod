package com.wwf.projectmanagement.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.wwf.projectmanagement.R

/** Copy and artwork for the "info" section at the bottom of a project page. */
data class ProjectInfo(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @StringRes val detailRes: Int,
    @DrawableRes val paintingRes: Int,
    @StringRes val paintingDescriptionRes: Int,
    @StringRes val captionTitleRes: Int,
    @StringRes val captionCreditRes: Int? = null,
    @StringRes val captionBodyRes: Int,
    /** The web only shows the "Locations" stat for the Straw Headed Bulbul project. */
    val showLocations: Boolean = true,
)

/** A conservation project: listed on the home page and given its own project page. */
data class Project(
    val id: String,
    @StringRes val nameRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val imageRes: Int,
    val info: ProjectInfo,
    /** Survey events and Telegram settings are "Coming soon" for Rifle Range Road on the web. */
    val surveyToolsAvailable: Boolean = true,
)

object Projects {
    const val STRAW_HEADED_BULBUL_ID = "straw-headed-bulbul"
    const val RIFLE_RANGE_ROAD_ID = "rifle-range-road"

    val strawHeadedBulbul = Project(
        id = STRAW_HEADED_BULBUL_ID,
        nameRes = R.string.project_shb_name,
        descriptionRes = R.string.project_shb_description,
        imageRes = R.drawable.project_straw_headed_bulbul,
        info = ProjectInfo(
            titleRes = R.string.shb_info_title,
            descriptionRes = R.string.shb_info_description,
            detailRes = R.string.info_detail,
            paintingRes = R.drawable.painting_feng_yun,
            paintingDescriptionRes = R.string.shb_painting_description,
            captionTitleRes = R.string.shb_caption_title,
            captionCreditRes = R.string.shb_caption_credit,
            captionBodyRes = R.string.shb_caption_body,
        ),
    )

    val rifleRangeRoad = Project(
        id = RIFLE_RANGE_ROAD_ID,
        nameRes = R.string.project_rrr_name,
        descriptionRes = R.string.project_rrr_description,
        imageRes = R.drawable.project_rifle_range_road,
        info = ProjectInfo(
            titleRes = R.string.rrr_info_title,
            descriptionRes = R.string.rrr_info_description,
            detailRes = R.string.info_detail_rrr,
            paintingRes = R.drawable.project_rifle_range_road,
            paintingDescriptionRes = R.string.rrr_painting_description,
            captionTitleRes = R.string.rrr_caption_title,
            captionBodyRes = R.string.rrr_caption_body,
            showLocations = false,
        ),
        surveyToolsAvailable = false,
    )

    val all: List<Project> = listOf(strawHeadedBulbul, rifleRangeRoad)

    fun byId(id: String?): Project? = all.firstOrNull { it.id == id }
}
