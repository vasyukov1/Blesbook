package com.vasyukov.bookapp

import android.os.Parcel
import android.os.Parcelable

data class Book(
    var title: String,
    var author: String,
    var country: String,
    var year: Int,
    var quotes: MutableList<String>,
    var isRead: Boolean,
    var isWishlist: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.createStringArrayList()?.toMutableList() ?: mutableListOf(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(author)
        parcel.writeString(country)
        parcel.writeInt(year)
        parcel.writeStringList(quotes)
        parcel.writeByte(if (isRead) 1 else 0)
        parcel.writeByte(if (isWishlist) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }
}
