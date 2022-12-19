package com.example.picoff.models

data class PendingChallengeModel (
    val challengeId: String? = null,
    val challengeTitle: String? = null,
    val challengeDesc: String? = null,
    val nameChallenger: String? = null,
    val nameRecipient: String? = null,
    val status: String? = null
)

enum class ChallengeStatus {
    SENT, OPEN, VOTE, RESULT
}
