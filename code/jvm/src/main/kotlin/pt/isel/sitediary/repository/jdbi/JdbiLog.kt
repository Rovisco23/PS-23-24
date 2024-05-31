package pt.isel.sitediary.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.sitediary.domainmodel.work.LogEntry
import pt.isel.sitediary.model.FileModel
import pt.isel.sitediary.model.LogInputModel
import pt.isel.sitediary.repository.LogRepository
import java.sql.Timestamp
import java.util.*

class JdbiLog(private val handle: Handle) : LogRepository {

    override fun createLog(
        log: LogInputModel,
        createdAt: Timestamp,
        author: Int,
        images: List<FileModel>?,
        docs: List<FileModel>?
    ): Int {
        val rId = handle.createUpdate(
            "insert into registo(oId, titulo, texto, estado, creation_date, author)" +
                    "values (:workId, :title, :description, :state, :createdAt, :author)"
        )
            .bind("workId", log.workId)
            .bind("title", log.title)
            .bind("description", log.description)
            .bind("state", "EDITÁVEL")
            .bind("createdAt", createdAt)
            .bind("author", author)
            .executeAndReturnGeneratedKeys()
            .mapTo(Int::class.java)
            .one()
        images?.forEach { img -> inputImagesToLog(rId, log.workId, img, createdAt) }
        docs?.forEach { doc -> inputDocsToLog(rId, log.workId, doc, createdAt) }
        return rId
    }

    override fun getById(id: Int): LogEntry? = handle.createQuery(
        "select REGISTO.id, titulo, REGISTO.oid, texto, estado, creation_date, last_modification_date, author, " +
                "username, MEMBRO.role from REGISTO join UTILIZADOR on UTILIZADOR.id = author " +
                "join MEMBRO on MEMBRO.uid = author where Registo.id = :id"
    )
        .bind("id", id)
        .mapTo(LogEntry::class.java)
        .singleOrNull()

    override fun checkUserAccess(workId: UUID, userId: Int): Boolean = handle.createQuery(
        "select count(*) from MEMBRO where oId = :oId and uId = :uId"
    )
        .bind("oId", workId.toString())
        .bind("uId", userId)
        .mapTo(Int::class.java)
        .single() == 1

    override fun getImages(logId: Int): List<FileModel>? = handle.createQuery(
        "select name, type, file from IMAGEM where rId = :rId"
    )
        .bind("rId", logId)
        .mapTo(FileModel::class.java)
        .list()

    override fun getDocs(logId: Int): List<FileModel>? = handle.createQuery(
        "select name, type, file from DOCUMENTO where rId = :rId"
    )
        .bind("rId", logId)
        .mapTo(FileModel::class.java)
        .list()

    override fun finish(logId: Int) {
        handle.createUpdate(
            "update REGISTO set estado = 'FINISHED' where id = :id"
        )
            .bind("id", logId)
            .execute()
    }

    override fun editLog(logId: Int, logInfo: LogInputModel, modifiedAt: Timestamp, images: List<FileModel>?, docs: List<FileModel>?) {
        handle.createUpdate(
            "update REGISTO set titulo = :title, texto = :description, last_modification_date = :modDate " +
                    "where id = :id"
        )
            .bind("title", logInfo.title)
            .bind("description", logInfo.description)
            .bind("modDate", Timestamp(System.currentTimeMillis()))
            .bind("id", logId)
            .execute()
        images?.forEach { img -> inputImagesToLog(logId, logInfo.workId, img, modifiedAt) }
        docs?.forEach { doc -> inputDocsToLog(logId, logInfo.workId, doc, modifiedAt) }
    }

    private fun inputImagesToLog(rId: Int, workId: UUID, images: FileModel, uploadDate: Timestamp) {
        handle.createUpdate(
            "insert into IMAGEM(rId, oId, name, type, file, upload_date) values " +
                    "(:rId, :oId, :name, :type, :file, :upload_date)"
        )
            .bind("rId", rId)
            .bind("oId", workId)
            .bind("name", images.fileName)
            .bind("type", images.contentType)
            .bind("file", images.file)
            .bind("upload_date", uploadDate)
            .execute()
    }

    private fun inputDocsToLog(rId: Int, workId: UUID, doc: FileModel, uploadDate: Timestamp) {
        handle.createUpdate(
            "insert into DOCUMENTO(rId, oId, name, type, file, upload_date) values " +
                    "(:rId, :oId, :name, :type, :file, :upload_date)"
        )
            .bind("rId", rId)
            .bind("oId", workId)
            .bind("name", doc.fileName)
            .bind("type", doc.contentType)
            .bind("file", doc.file)
            .bind("upload_date", uploadDate)
            .execute()
    }
}