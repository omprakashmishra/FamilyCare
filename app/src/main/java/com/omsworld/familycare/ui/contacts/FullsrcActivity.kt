package com.omsworld.familycare.ui.contacts

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.omsworld.familycare.R

class FullsrcActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_image_ac)

        val image: ImageView = findViewById(R.id.IV_image)
        val backButton: ImageView = findViewById(R.id.IV_backimage)

        val url = intent.getStringExtra("img_url")

        if (!url.isNullOrBlank()) {
            image.load(url) {
                placeholder(R.drawable.ic_profile)
                error(R.drawable.ic_profile)
                crossfade(true)
            }
        }

        backButton.setOnClickListener { finish() }
    }
}