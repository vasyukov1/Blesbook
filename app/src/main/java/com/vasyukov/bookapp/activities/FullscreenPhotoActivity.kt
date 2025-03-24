package com.vasyukov.bookapp.activities

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.vasyukov.bookapp.R

class FullscreenPhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_photo)

        val fullscreenImageView: ImageView = findViewById(R.id.fullscreenImageView)
        val btnClose: ImageButton = findViewById(R.id.btnClose)

        // Получение пути к фотографии
        val photoPath = intent.getStringExtra("photo_path")

        // Загрузка фото
        if (photoPath != null) {
            Glide.with(this)
                .load(photoPath)
                .into(fullscreenImageView)
        }

        // Обработка нажатия на кнопку закрытия
        btnClose.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }
    }
}