package com.example.picoff.models

data class PendingChallengeModel (
    val challengeId: String? = null,
    val challengeTitle: String? = null,
    val challengeDesc: String? = null,
    val uidChallenger: String? = null,
    val uidRecipient: String? = null,
    val status: String? = null,
    var nameChallenger: String? = null,
    var nameRecipient: String? = null,
    var photoUrlChallenger: String? = null,
    var photoUrlRecipient: String? = null,
)

enum class ChallengeStatus {
    SENT, OPEN, VOTE, RESULT
}
