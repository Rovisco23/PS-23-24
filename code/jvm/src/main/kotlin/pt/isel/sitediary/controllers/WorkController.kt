package pt.isel.sitediary.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import pt.isel.sitediary.domainmodel.authentication.AuthenticatedUser
import pt.isel.sitediary.domainmodel.work.OpeningTerm
import pt.isel.sitediary.domainmodel.work.Work
import pt.isel.sitediary.model.FileModel
import pt.isel.sitediary.model.InviteInputModel
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
        val res = service.getWork(id, user.user)
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
        val res = service.getWorkList(skip, user.user)
        return handleResponse(res) {
            ResponseEntity.ok(it)
        }
    }

    @PostMapping(Paths.Invite.GET_INVITE)
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

    @GetMapping(Paths.Invite.GET_INVITE_LIST)
    fun getInviteList(@Parameter(hidden = true) user: AuthenticatedUser): ResponseEntity<*> {
        val res = service.getInviteList(user.user.id)
        return handleResponse(res) {
            ResponseEntity.status(200).body(it)
        }
    }

    @GetMapping(Paths.Invite.GET_INVITE)
    fun getInvite(@PathVariable id: UUID, @Parameter(hidden = true) user: AuthenticatedUser): ResponseEntity<*> {
        val res = service.getInvite(id, user.user.id)
        return handleResponse(res) {
            ResponseEntity.status(200).body(it)
        }
    }

    @PutMapping(Paths.Invite.GET_INVITE_LIST)
    fun answerInvite(
        @RequestBody body: InviteInputModel,
        @Parameter(hidden = true) user: AuthenticatedUser
    ): ResponseEntity<*> {
        val res = service.answerInvite(body, user.user)
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
    @Operation(
        summary = "Get opening term of a specific work",
        description = "Used to fetch the opening term of a work"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Opening term received successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = OpeningTerm::class)
                    )
                ]
            )
        ]
    )
    fun getOpeningTerm(@PathVariable id: UUID, @Parameter(hidden = true) user: AuthenticatedUser)
            : ResponseEntity<*> {
        val res = service.getOpeningTerm(id, user.user)
        return handleResponse(res) {
            /*val htmlContent = String(Files.readAllBytes(java.nio.file.Paths.get("docs\\OpeningTerm.html")))
            val outPutStream = ByteArrayOutputStream()
            PdfRendererBuilder()
                .useFastMode()
                .withHtmlContent(htmlContent, null)
                .toStream(outPutStream)
                .run()
            ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "Attachment;filename=TermoAbertura.pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(ByteArrayResource(outPutStream.toByteArray()))*/
            ResponseEntity.ok(it)
        }
    }

    @PostMapping(Paths.Work.FINISH_WORK)
    @Operation(summary = "Finish Work", description = "Concludes the Work and creates a closing term for that work")
    fun finishWork(@RequestParam work: UUID, @Parameter(hidden = true) user: AuthenticatedUser)
            : ResponseEntity<*> {
        val res = service.finishWork(work, user.user.id)
        return handleResponse(res) {
            ResponseEntity.ok(it)
        }
    }

    @GetMapping(Paths.Work.GET_IMAGE)
    @Operation(summary = "Get Work Featured Image", description = "Gets the featured image of a work")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Successful featured image retrieval",
                content = [
                    Content(schema = Schema(implementation = FileModel::class))
                ]
            )
        ]
    )
    fun getWorkImage(@PathVariable id: UUID, @Parameter(hidden = true) user: AuthenticatedUser)
            : ResponseEntity<*> {
        val res = service.getWorkImage(id, user.user)
        return handleResponse(res) {
            if (it == null) ResponseEntity.ok().body(null)
            else ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "Attachment;filename=${it.fileName}")
                .contentType(MediaType.parseMediaType(it.contentType))
                .body(ByteArrayResource(it.file))
        }
    }

    @PutMapping(Paths.Work.GET_IMAGE, consumes = ["multipart/form-data"])
    @Operation(
        summary = "Change work featured image",
        description = "Operation used to change the featured image of a work"
    )
    fun changeWorkImage(
        @PathVariable id: UUID,
        @RequestParam("file") file: MultipartFile?,
        @Parameter(hidden = true) user: AuthenticatedUser
    ): ResponseEntity<*> {
        val featuredImage = if (file == null) null else
            FileModel(
                file.bytes,
                file.originalFilename!!,
                file.contentType!!
            )
        val res = service.changeWorkImage(id, featuredImage, user.user.id)
        return handleResponse(res) {
            ResponseEntity.ok().header("Location", "/work/${id}").body(Unit)
        }
    }
}