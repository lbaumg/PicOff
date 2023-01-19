package com.example.picoff.models

import android.os.Parcel
import android.os.Parcelable

data class ChallengeModel(
    val challengeId: String? = "",
    val challengeTitle: String? = "",
    val challengeDesc: String? = "",
    val creatorId: String? = "",
    val upvotes: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(challengeId)
        parcel.writeString(challengeTitle)
        parcel.writeString(challengeDesc)
        parcel.writeString(creatorId)
        parcel.writeInt(upvotes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChallengeModel> {
        override fun createFromParcel(parcel: Parcel): ChallengeModel {
            return ChallengeModel(parcel)
        }

        override fun newArray(size: Int): Array<ChallengeModel?> {
            return arrayOfNulls(size)
        }
    }
}
