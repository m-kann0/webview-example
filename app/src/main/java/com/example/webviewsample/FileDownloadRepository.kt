package com.example.webviewsample

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.*

class FileDownloadRepository(private val context: Context) {

    fun download(url: String, listener: (File?) -> Unit) {
        val downloadService = Retrofit.Builder()
            .baseUrl("http://example.com/") // @Urlで「https://〜」のフルURLを渡す場合、baseUrlはなんでもいいっぽい
            .build()
            .create(FileDownloadService::class.java) // 本来はInjectionで取得する

        downloadService.downloadFileWithDynamicUrlAsync(url).enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!response.isSuccessful) {
                    Log.d(TAG, "server contact failed")
                    return
                }

                // 本当はAsyncTaskではなくAppExecutors.diskIOとLiveData<Resource<File>>を使う
                object : AsyncTask<Void, Void, File?>() {
                    override fun doInBackground(vararg params: Void?): File? {
                        return writeResponseBodyToDisk(response.body())
                    }

                    override fun onPostExecute(result: File?) {
                        result ?: return
                        listener(result)
                    }
                }.execute()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "error")
            }
        })
    }

    private fun writeResponseBodyToDisk(body: ResponseBody?): File? {
        body ?: return null

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            val buff = ByteArray(4096)

            val fileSize = body.contentLength()
            var fileSizeDownloaded = 0

            inputStream = body.byteStream()
            outputStream = context.openFileOutput(TEMP_FILE_NAME, Context.MODE_PRIVATE)

            var read = inputStream.read(buff)
            while (read != -1) {
                outputStream.write(buff, 0, read)

                fileSizeDownloaded += read
                Log.d(TAG, "file download: $fileSizeDownloaded of $fileSize")

                read = inputStream.read(buff)
            }

            outputStream.flush()

            return File(context.filesDir, TEMP_FILE_NAME)
        } catch (e: IOException) {
            return null
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }

    companion object {

        private const val TAG = "FileDownloadRepository"

        private const val TEMP_FILE_NAME = "file.pdf"
    }
}
