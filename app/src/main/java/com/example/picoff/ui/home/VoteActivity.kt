package com.example.picoff.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.picoff.R
import com.google.firebase.storage.FirebaseStorage

class VoteActivity : AppCompatActivity() {


    private lateinit var ivImageChallenger: ImageView
    private lateinit var ivImageRecipient: ImageView
    private lateinit var ivVs1: ImageView
    private lateinit var ivVs2: ImageView
    private lateinit var layoutVsScreen: RelativeLayout

    private var storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://picoff-5abdb.appspot.com/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote)

        val urlImgChallenger = intent.getStringExtra("urlImgChallenger")
        val urlImgRecipient = intent.getStringExtra("urlImgRecipient")

        ivImageChallenger = findViewById(R.id.ivChallengeImageChallenger)
        ivImageRecipient = findViewById(R.id.ivChallengeImageRecipient)
        ivVs1 = findViewById(R.id.ivChallengeVs1)
        ivVs2 = findViewById(R.id.ivChallengeVs2)
        layoutVsScreen = findViewById(R.id.layoutVsScreen)

        Glide.with(this).load(urlImgChallenger).into(ivImageChallenger)
        Glide.with(this).load(urlImgRecipient).into(ivImageRecipient)
        Glide.with(this).load(urlImgChallenger).centerCrop().into(ivVs1)
        Glide.with(this).load(urlImgRecipient).centerCrop().into(ivVs2)

        ivImageChallenger.setOnClickListener {
            ivImageChallenger.visibility = View.INVISIBLE
            ivImageRecipient.visibility = View.VISIBLE
        }

        ivImageRecipient.setOnClickListener {
            ivImageRecipient.visibility = View.INVISIBLE
            layoutVsScreen.visibility = View.VISIBLE
        }

        ivVs1.setOnClickListener {
            // vote for challenger
            voteAndFinish(1)
        }

        ivVs2.setOnClickListener {
            // vote for recipient
            voteAndFinish(2)
        }
    }

    private fun voteAndFinish(vote: Int) {
        var data = Intent()
        data.putExtra("vote", vote)
        setResult(RESULT_OK, data)
        finish()
    }


    override fun finish() {
        super.finish()
        ivImageChallenger.visibility = View.VISIBLE
        ivImageRecipient.visibility = View.INVISIBLE
        layoutVsScreen.visibility = View.INVISIBLE
    }

}