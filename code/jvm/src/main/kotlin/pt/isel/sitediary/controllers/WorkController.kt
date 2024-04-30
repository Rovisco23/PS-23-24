package pt.isel.sitediary.controllers

import kotlinx.serialization.Serializable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.isel.sitediary.service.WorkService
import pt.isel.sitediary.utils.AuthenticatedUser
import pt.isel.sitediary.utils.Paths
import pt.isel.sitediary.utils.Tecnitian
import pt.isel.sitediary.utils.handleResponse
import java.util.*


@RestController
class WorkController(private val service: WorkService) {

    @GetMapping(Paths.Work.GET_BY_ID)
    fun getWorkById(@PathVariable id: UUID, user: AuthenticatedUser): ResponseEntity<*> {
        val res = service.getWork(id, user.user.id)
        return handleResponse(res){
            ResponseEntity.status(200).body(it)
        }
    }

    @GetMapping(Paths.Work.GET_ALL_WORKS)
    fun getWorkList(@RequestParam skip: Int, user: AuthenticatedUser): ResponseEntity<*> {
        val res = service.getWorkList(skip, user.user.id)
        return handleResponse(res) {
            ResponseEntity.status(200).body(it)
        }
    }

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