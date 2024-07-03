package pt.isel.sitediary.services

import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import pt.isel.sitediary.domain.LogEntry
import pt.isel.sitediary.domain.LogInputModel
import pt.isel.sitediary.ui.common.LogCreationException
import pt.isel.sitediary.ui.common.LogException
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LogService(
    private val client: OkHttpClient,
    private val gson: Gson,
    url: String
) {
    private val templateURL = "$url/api"

    suspend fun getLogById(id: Int, token: String): LogEntry {
        val request = Request.Builder()
            .url("$templateURL/logs/$id")
            .header("accept", "application/json")
            .header("Authorization", "Bearer $token")
            .get()
            .build()
        return suspendCoroutine {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    it.resumeWithException(LogException("Failed to get Log", e))
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body
                    if (!response.isSuccessful || body == null) {
                        if (body != null) {
                            it.resumeWithException(LogException(body.string()))
                        } else it.resumeWithException(LogException("Failed to get Log"))
                    } else it.resume(gson.fromJson(body.string(), LogEntry::class.java))
                }
            })
        }
    }

    suspend fun createLog(input: LogInputModel, workId: String, token: String) {

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "log",
                null,
                """{
                "workId": "$workId",
                "title": "${input.title}",
                "description": "${input.description}"
                }""".trimMargin().toRequestBody("application/json".toMediaTypeOrNull())
            )
            .build()

        val request = Request.Builder()
            .url("$templateURL/logs")
            .header("accept", "application/json")
            .header("Authorization", "Bearer $token")
            .post(requestBody)
            .build()
        return suspendCoroutine {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    it.resumeWithException(LogCreationException("Failed to get Log", e))
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body
                    if (!response.isSuccessful) {
                        if (body != null) {
                            it.resumeWithException(LogCreationException(body.string()))
                        } else it.resumeWithException(LogCreationException("Failed to get Log"))
                    } else it.resume(Unit)
                }
            })
        }
    }
}