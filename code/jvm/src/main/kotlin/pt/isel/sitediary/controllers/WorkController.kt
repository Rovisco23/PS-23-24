package pt.isel.sitediary.controllers

import kotlinx.serialization.Serializable
import org.springframework.web.bind.annotation.RestController
import pt.isel.sitediary.utils.Tecnitian
import java.awt.Image


@RestController
class WorkController {

    @Serializable
    data class OpeningTerm(
        //val image: Image,
        val name: String,
        val type: String,
        val description: String?,
        val holder: String,
        val director: String,
        val company: String,
        val companyNum: Int,
        val building: String,
        val parish: String,
        val street: String,
        val postalCode: String,
        val members: List<Tecnitian>
    )

}