package com.omsworld.familycare.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.omsworld.familycare.R

class ViewImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_image_ac)

        val image = findViewById<ImageView>(R.id.IV_image)
        val progress = findViewById<View>(R.id.mprogressBar)
        val back = findViewById<View>(R.id.IV_backimage)

        val url = intent.getStringExtra("ImageUrl")
        if (!url.isNullOrBlank()) {
            image.load(url) {
                listener(onSuccess = { _, _ -> progress.visibility = View.GONE },
                    onError = { _, _ -> progress.visibility = View.GONE })
            }
        }

        back?.setOnClickListener { finish() }
    }
}