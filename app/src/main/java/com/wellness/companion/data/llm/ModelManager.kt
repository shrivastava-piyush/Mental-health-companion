package com.wellness.companion.data.llm

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class ModelManager(private val context: Context) {

    sealed class Status {
        data object NotDownloaded : Status()
        data class Downloading(val progress: Float) : Status()
        data object Ready : Status()
        data class Error(val message: String) : Status()
    }

    private val _status = MutableStateFlow<Status>(Status.NotDownloaded)
    val status: StateFlow<Status> = _status.asStateFlow()

    private val modelDir = File(context.filesDir, "llm")
    private val modelFile = File(modelDir, MODEL_FILENAME)

    init {
        if (modelFile.exists() && modelFile.length() > MIN_VALID_SIZE) {
            _status.value = Status.Ready
        }
    }

    fun modelPath(): String = modelFile.absolutePath
    fun isDownloaded(): Boolean = _status.value is Status.Ready

    suspend fun download(url: String) = withContext(Dispatchers.IO) {
        if (_status.value is Status.Downloading) return@withContext
        _status.value = Status.Downloading(0f)

        try {
            modelDir.mkdirs()
            val tempFile = File(modelDir, "$MODEL_FILENAME.part")

            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 30_000
            connection.readTimeout = 30_000

            if (tempFile.exists() && tempFile.length() > 0) {
                connection.setRequestProperty("Range", "bytes=${tempFile.length()}-")
            }

            connection.connect()

            val totalSize = when (connection.responseCode) {
                HttpURLConnection.HTTP_PARTIAL -> {
                    val range = connection.getHeaderField("Content-Range")
                    range?.substringAfter("/")?.toLongOrNull() ?: (connection.contentLength.toLong() + tempFile.length())
                }
                HttpURLConnection.HTTP_OK -> {
                    tempFile.delete()
                    connection.contentLength.toLong()
                }
                else -> {
                    _status.value = Status.Error("HTTP ${connection.responseCode}")
                    return@withContext
                }
            }

            val append = connection.responseCode == HttpURLConnection.HTTP_PARTIAL

            connection.inputStream.use { input ->
                FileOutputStream(tempFile, append).use { output ->
                    val buffer = ByteArray(8192)
                    var downloaded = if (append) tempFile.length() else 0L
                    while (true) {
                        val bytes = input.read(buffer)
                        if (bytes == -1) break
                        output.write(buffer, 0, bytes)
                        downloaded += bytes
                        _status.value = Status.Downloading(
                            if (totalSize > 0) downloaded.toFloat() / totalSize else 0f,
                        )
                    }
                }
            }

            tempFile.renameTo(modelFile)
            _status.value = Status.Ready
        } catch (e: Exception) {
            _status.value = Status.Error(e.message ?: "Download failed")
        }
    }

    fun deleteModel() {
        modelFile.delete()
        File(modelDir, "$MODEL_FILENAME.part").delete()
        _status.value = Status.NotDownloaded
    }

    companion object {
        const val MODEL_FILENAME = "reflection-model.gguf"
        const val MIN_VALID_SIZE = 50_000_000L
    }
}
