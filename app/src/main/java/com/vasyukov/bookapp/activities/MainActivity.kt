package com.vasyukov.bookapp.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.vasyukov.bookapp.R

class MainActivity : AppCompatActivity() {

    private lateinit var fioLabel: TextView
    private lateinit var groupLabel: TextView
    private lateinit var iconImage: ImageView
    private lateinit var avatarImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Инициализовация элементов
        fioLabel = findViewById<TextView>(R.id.fioLabel)
        groupLabel = findViewById<TextView>(R.id.groupLabel)
        iconImage = findViewById<ImageView>(R.id.iconImage)
        avatarImage = findViewById<ImageView>(R.id.avatarImage)

        // Анимация появления
        listOf(fioLabel, groupLabel, iconImage, avatarImage).forEach { view ->
            ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
                duration = 1000
                start()
            }
        }

        // Анимация закрытия
        Handler(Looper.getMainLooper()).postDelayed({
            listOf(iconImage, fioLabel, groupLabel, avatarImage).forEach { element ->
                ObjectAnimator.ofFloat(element, "alpha", 1f, 0f).apply {
                    duration = 1000
                }
            }
            val fadeOutIcon = ObjectAnimator.ofFloat(iconImage, "alpha", 1f, 0f).apply {
                duration = 1000
            }
            val moveFioDown = ObjectAnimator.ofFloat(fioLabel, "alpha", 1f, 0f).apply {
                duration = 1000
            }
            val moveGroupDown = ObjectAnimator.ofFloat(groupLabel, "alpha", 1f, 0f).apply {
                duration = 1000
            }
            val fadeOutAvatar = ObjectAnimator.ofFloat(avatarImage, "alpha", 1f, 0f).apply {
                duration = 1000
            }

            fadeOutAvatar.addUpdateListener {
                if (it.animatedFraction == 1f) {
                    iconImage.visibility = ImageView.GONE
                    fioLabel.visibility = TextView.GONE
                    groupLabel.visibility = TextView.GONE
                    avatarImage.visibility = TextView.GONE

                    listOf(fioLabel, groupLabel).forEach { element ->
                        ObjectAnimator.ofFloat(element, "alpha", 0f, 1f).apply {
                            duration = 1000
                            start()
                        }
                    }
                }
            }

            val intent = Intent(this, HomeActivity::class.java)

            AnimatorSet().apply {
                playTogether(fadeOutAvatar, moveFioDown, moveGroupDown ,fadeOutIcon)
                start()
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }, 2000)
    }
}