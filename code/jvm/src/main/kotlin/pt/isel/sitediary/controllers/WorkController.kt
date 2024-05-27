package pt.isel.sitediary.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.isel.sitediary.domainmodel.authentication.AuthenticatedUser
import pt.isel.sitediary.domainmodel.work.Work
import pt.isel.sitediary.model.InviteResponseModel
import pt.isel.sitediary.model.ListOfWorksOutputModel
import pt.isel.sitediary.model.MemberInputModel
import pt.isel.sitediary.model.OpeningTermInputModel
import pt.isel.sitediary.service.WorkService
import pt.isel.sitediary.utils.Errors
import pt.isel.sitediary.utils.Paths
import pt.isel.sitediary.utils.handleResponse
import java.net.URI
import java.util.*


@RestController
@Tag(name = "Work", description = "Operations related to Work.")
class WorkController(private val service: WorkService) {

    @GetMapping(Paths.Work.GET_BY_ID)
    @Operation(summary = "Get work by Id", description = "Used to request a work by its id")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Work received successfully",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = Work::class))
                ]
            ),
            ApiResponse(
                responseCode = "401", description = "You are not a member of this Work",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = Errors::class))
                ]
            ),
            ApiResponse(
                responseCode = "404", description = "Work does not exist",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = Errors::class))
                ]
            )
        ]
    )
    fun getWorkById(@PathVariable id: UUID, @Parameter(hidden = true) user: AuthenticatedUser): ResponseEntity<*> {
        val res = service.getWork(id, user.user.id)
        return handleResponse(res) {
            ResponseEntity.ok(it)
        }
    }

    @GetMapping(Paths.Work.GET_ALL_WORKS)
    @Operation(summary = "Get all works", description = "Used to request all works from a user")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "List of work received successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ListOfWorksOutputModel::class)
                    )
                ]
            )
        ]
    )
    fun getWorkList(@RequestParam skip: Int, @Parameter(hidden = true) user: AuthenticatedUser): ResponseEntity<*> {
        val res = service.getWorkList(skip, user.user.id)
        return handleResponse(res) {
            ResponseEntity.ok(it)
        }
    }

    @PostMapping(Paths.Work.GET_BY_ID)
    @Operation(summary = "Invite Members", description = "Used to to invite users to a work")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Invites sent successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = String::class)
                    )
                ]
            )
        ]
    )
    fun inviteMembers(
        @PathVariable id: UUID,
        @RequestBody members: List<MemberInputModel>,
        @Parameter(hidden = true) user: AuthenticatedUser
    ): ResponseEntity<*> {
        val res = service.inviteMembers(members, id, user.user.id)
        return handleResponse(res) {
            ResponseEntity.status(200).body(it)
        }
    }

    @GetMapping(Paths.Work.GET_INVITE_LIST)
    fun getInviteList(@Parameter(hidden = true) user: AuthenticatedUser): ResponseEntity<*> {
        val res = service.getInviteList(user.user.id)
        return handleResponse(res) {
            ResponseEntity.status(200).body(it)
        }
    }

    @GetMapping(Paths.Work.GET_INVITE)
    fun getInvite(@PathVariable id: UUID, @Parameter(hidden = true) user: AuthenticatedUser): ResponseEntity<*> {
        val res = service.getInvite(id, user.user.id)
        return handleResponse(res) {
            ResponseEntity.status(200).body(it)
        }
    }

    @PostMapping(Paths.Work.GET_INVITE_LIST)
    fun answerInvite(
        @RequestBody resp: InviteResponseModel,
        @Parameter(hidden = true) user: AuthenticatedUser
    ): ResponseEntity<*> {
        val res = service.inviteResponse(resp, user.user.id)
        return handleResponse(res) {
            ResponseEntity.status(200).body(it)
        }
    }

    @PostMapping(Paths.Work.GET_ALL_WORKS)
    @Operation(summary = "Start Work", description = "Used to start a new work")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201", description = "Work created successfully",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = Unit::class))
                ]
            ),
            ApiResponse(
                responseCode = "400", description = "Invalid parameters",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = Errors::class))
                ]
            )
        ]
    )
    fun createWork(@RequestBody work: OpeningTermInputModel, @Parameter(hidden = true) user: AuthenticatedUser)
            : ResponseEntity<*> {
        val res = service.createWork(work, user.user)
        return handleResponse(res) {
            ResponseEntity.created(URI.create("/works")).body(it)
        }
    }

    @GetMapping(Paths.Work.GET_OPENING_TERM)
    fun getOpeningTerm(@PathVariable work: UUID, @Parameter(hidden = true) user: AuthenticatedUser)
            : ResponseEntity<*> {
        val res = service.getOpeningTerm(work, user.user.id)
        return handleResponse(res) {
            ResponseEntity.ok(it)
        }
    }

    fun finishWork(@RequestParam work: UUID, @Parameter(hidden = true) user: AuthenticatedUser)
            : ResponseEntity<*> {
        service.finishWork(work, user.user.id)
        return ResponseEntity.ok("Work finished successfully")
    }

}