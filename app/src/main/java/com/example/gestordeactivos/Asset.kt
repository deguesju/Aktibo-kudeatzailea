package com.example.gestordeactivos

import android.os.Parcel
import android.os.Parcelable

data class Asset(
    val name: String,
    val symbol: String,
    val value: String,
    var percent: String,
    val iconColor: String,
    val showIcon: Boolean = true,
    var isSelected: Boolean = false,
    var assignedPercent: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(symbol)
        parcel.writeString(value)
        parcel.writeString(percent)
        parcel.writeString(iconColor)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeInt(assignedPercent)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Asset> {
        override fun createFromParcel(parcel: Parcel): Asset = Asset(parcel)
        override fun newArray(size: Int): Array<Asset?> = arrayOfNulls(size)
    }
}
