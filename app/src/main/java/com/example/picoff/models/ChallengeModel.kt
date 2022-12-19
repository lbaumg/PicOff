package com.example.picoff.models

data class ChallengeModel (
    val challengeId: String = "",
    val challengeTitle: String = "",
    val challengeDesc: String = "",
    val creatorId: String = "",
    val upvotes: Int = 0
)