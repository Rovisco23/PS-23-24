package pt.isel.sitediary.domainmodel.work

data class OpeningTermVerification(
    val doc: String,
    val signature: String,
    val dt_signature: String
)

data class OpeningTermLocation(
    val county: String,
    val parish: String,
    val street: String,
    val postalCode: String,
    val building: String
)

data class OpeningTermAuthor(
    val name: String,
    val association: String,
    val num: Int
)

data class OpeningTermHTML(
    val verification: OpeningTermVerification,
    val location: OpeningTermLocation,
    val licenseHolder: String,
    val authors: Map<String, OpeningTermAuthor>,
    /*val fiscalization: OpeningTermAuthor,
    val coordinator: OpeningTermAuthor,
    val architect: OpeningTermAuthor,
    val stability: OpeningTermAuthor,
    val electricity: OpeningTermAuthor,
    val gas: OpeningTermAuthor,
    val water: OpeningTermAuthor,
    val phone: OpeningTermAuthor,
    val isolation: OpeningTermAuthor,
    val acoustic: OpeningTermAuthor,
    val transport: OpeningTermAuthor,
    val director: OpeningTermAuthor,*/
    val company: ConstructionCompany,
    val type: String,
) {
}