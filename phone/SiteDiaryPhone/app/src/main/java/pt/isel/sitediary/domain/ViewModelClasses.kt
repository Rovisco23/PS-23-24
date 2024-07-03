package pt.isel.sitediary.domain

data class WorkListAndProfile(
    val workList: List<WorkSimplified>,
    val profile: Profile
)