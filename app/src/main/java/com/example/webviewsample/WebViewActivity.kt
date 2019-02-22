package com.example.webviewsample

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import java.io.File

class WebViewActivity : AppCompatActivity() {

    private val fileDownloadRepository = FileDownloadRepository(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val url: String = intent.getStringExtra(URL)

        val webView = findViewById<WebView>(R.id.web_view)

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val urlString = request!!.url.toString()
                Log.d(TAG, "URL: $urlString")

                if (urlString.endsWith(".pdf")) {
                    // ダウンロード中はくるくる表示するべきだがしてない
                    downloadAndShowPdf(urlString)
                    return true
                }

                return false
            }
        }

        webView.loadUrl(url)
    }

    private fun downloadAndShowPdf(urlString: String) {
        fileDownloadRepository.download(urlString) { file: File? ->
            file ?: return@download

            Log.d(TAG, "Downloaded File: ${file.absolutePath}")
            val uri = FileProvider.getUriForFile(this@WebViewActivity, FILE_PROVIDER_AUTHORITY, file)
            Log.d(TAG, "URI: $uri")

            val intent = Intent(Intent.ACTION_VIEW).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setDataAndType(uri, "application/pdf")
            }
            startActivity(intent)
        }
    }

    companion object {

        private const val TAG = "WebViewActivity"

        private const val FILE_PROVIDER_AUTHORITY = "com.example.webviewsample.fileprovider"

        private const val URL = "URL"

        fun newIntent(context: Context, url: String): Intent {
            return Intent(context, WebViewActivity::class.java).apply {
                putExtra(URL, url)
            }
        }
    }
}
