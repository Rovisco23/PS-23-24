package pt.isel.sitediary.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.sitediary.domainmodel.authentication.AuthenticatedUser
import pt.isel.sitediary.model.LogInputModel
import pt.isel.sitediary.model.LogOutputModel
import pt.isel.sitediary.service.LogService
import pt.isel.sitediary.utils.Errors
import pt.isel.sitediary.utils.Paths
import pt.isel.sitediary.utils.handleResponse

@RestController
@Tag(name = "Log", description = "Operations related the Logs.")
class LogController (private val service: LogService) {

    @PostMapping(Paths.Log.GET_ALL_LOGS)
    @Operation(summary = "Create Log", description = "Used to create a log for a specific work.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201", description = "Successful Log Creation"
            ),
            ApiResponse(
                responseCode = "401", description = "Not a Member of the Work",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = Errors::class))
                ]
            )
        ]
    )
    fun createLog(@RequestBody log: LogInputModel, @Parameter(hidden = true) user: AuthenticatedUser, response: HttpServletResponse) {
        service.createLog(log, user.user.id)
        response.addHeader(HttpHeaders.LOCATION, "/logs")
        response.status = 201
    }

    @PostMapping(Paths.Log.GET_BY_ID)
    @Operation(summary = "Get Log By Id", description = "Used to get the details of a log")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201", description = "Log received successfully"
            ),
            ApiResponse(
                responseCode = "401", description = "Not a Member of the Work",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = Errors::class))
                ]
            )
        ]
    )
    fun getLogById(@PathVariable id: Int, @Parameter(hidden = true) user: AuthenticatedUser): ResponseEntity<*> {
        val res = service.getLog(id, user.user.id)
        return handleResponse(res) {
            val log = LogOutputModel(
                id = it.id,
                title = it.title,
                content = it.content,
                state = it.state,
                createdAt = it.createdAt,
                lastModifiedAt = it.lastModifiedAt,
                author = it.author
            )
            ResponseEntity.status(200).body(log)
        }
    }
}