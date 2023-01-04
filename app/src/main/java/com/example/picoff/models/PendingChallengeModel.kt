package com.example.picoff.models

import android.os.Parcel
import android.os.Parcelable

data class PendingChallengeModel (
    var challengeId: String? = null,
    val challengeTitle: String? = null,
    val challengeDesc: String? = null,
    val uidChallenger: String? = null,
    var uidRecipient: String? = null,
    var status: String? = null,
    var nameChallenger: String? = null,
    var nameRecipient: String? = null,
    var photoUrlChallenger: String? = null,
    var photoUrlRecipient: String? = null,
    var challengeImageUrlChallenger: String? = null,
    var challengeImageUrlRecipient: String? = null,
    var additionalInfoChallenger: String? = null,
    var additionalInfoRecipient: String? = null,
    var voteChallenger: Int? = null,
    var voteRecipient: Int? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(challengeId)
        parcel.writeString(challengeTitle)
        parcel.writeString(challengeDesc)
        parcel.writeString(uidChallenger)
        parcel.writeString(uidRecipient)
        parcel.writeString(status)
        parcel.writeString(nameChallenger)
        parcel.writeString(nameRecipient)
        parcel.writeString(photoUrlChallenger)
        parcel.writeString(photoUrlRecipient)
        parcel.writeString(challengeImageUrlChallenger)
        parcel.writeString(challengeImageUrlRecipient)
        parcel.writeString(additionalInfoChallenger)
        parcel.writeString(additionalInfoRecipient)
        parcel.writeValue(voteChallenger)
        parcel.writeValue(voteRecipient)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PendingChallengeModel> {
        override fun createFromParcel(parcel: Parcel): PendingChallengeModel {
            return PendingChallengeModel(parcel)
        }

        override fun newArray(size: Int): Array<PendingChallengeModel?> {
            return arrayOfNulls(size)
        }
    }
}

//enum class Status {
//    SENT, OPEN, VOTE_RECIPIENT, VOTE_CHALLENGER, RESULT
//
//    fun getEnumAsString(status: Status) {
//
//    }
//}
