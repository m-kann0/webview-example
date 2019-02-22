package com.example.webviewsample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val url = findViewById<EditText>(R.id.url)
        findViewById<Button>(R.id.open_button).setOnClickListener {
            val intent = WebViewActivity.newIntent(this@MainActivity, url.text.toString())
            startActivity(intent)
        }
    }
}
