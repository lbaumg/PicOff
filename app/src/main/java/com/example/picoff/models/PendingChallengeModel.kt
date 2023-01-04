package com.example.picoff.models

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
)
