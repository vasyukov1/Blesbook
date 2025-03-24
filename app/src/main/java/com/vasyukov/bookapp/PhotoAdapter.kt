package com.vasyukov.bookapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vasyukov.bookapp.activities.FullscreenPhotoActivity

class PhotoAdapter(
    private val photoList: List<String>,
    private val onPhotoLongClick: (Int) -> Unit
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPhoto: ImageView = itemView.findViewById(R.id.ivPhoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoPath = photoList[position]
        Glide.with(holder.itemView.context)
            .load(photoPath)
            .into(holder.ivPhoto)

        // Обработка нажатия на фото
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, FullscreenPhotoActivity::class.java)
            intent.putExtra("photo_path", photoPath)
            holder.itemView.context.startActivity(intent)
        }

        // Обработка долгого нажатия на фото
        holder.itemView.setOnLongClickListener {
            onPhotoLongClick(position)
            true
        }
    }

    // Получение количества фотографий
    override fun getItemCount(): Int {
        return photoList.size
    }
}
