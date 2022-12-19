package com.example.picoff.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.picoff.R
import com.example.picoff.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var isFabMenuOpen = false
    private lateinit var fabBase: FloatingActionButton
    private lateinit var fab1: FloatingActionButton
    private lateinit var fab2: FloatingActionButton
    private lateinit var fab3: FloatingActionButton
    private lateinit var randomChallengeLayout: LinearLayout
    private lateinit var createNewLayout: LinearLayout
    private lateinit var listChallengesLayout: LinearLayout

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkIfLoggedInWithGoogle()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        fabBase = findViewById(R.id.fabBase)
        fab1 = findViewById(R.id.fabCreateNew)
        fab2 = findViewById(R.id.fabRandomChallenge)
        fab3 = findViewById(R.id.fabListChallenges)
        randomChallengeLayout = findViewById(R.id.randomChallengeLayout)
        createNewLayout = findViewById(R.id.createNewLayout)
        listChallengesLayout = findViewById(R.id.listChallengesLayout)

        fabBase.setOnClickListener {
            if (!isFabMenuOpen) expandFabMenu() else collapseFabMenu()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFabMenuOpen)
                    collapseFabMenu()
                else
                    finish()
            }
        })
    }

    private fun checkIfLoggedInWithGoogle() {
        auth = FirebaseAuth.getInstance()
        // If not signed in open SignInActivity
        if (auth.currentUser == null) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun expandFabMenu() {
        ViewCompat.animate(fabBase).rotation(45.0f).withLayer()
            .setDuration(300).setInterpolator(OvershootInterpolator(10.0f)).start()

        val fabOpenAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_open)
        randomChallengeLayout.startAnimation(fabOpenAnimation)
        createNewLayout.startAnimation(fabOpenAnimation)
        listChallengesLayout.startAnimation(fabOpenAnimation)

        fab1.isClickable = true
        fab2.isClickable = true
        fab3.isClickable = true
        isFabMenuOpen = true
    }

    private fun collapseFabMenu() {
        ViewCompat.animate(fabBase).rotation(0.0f).withLayer()
            .setDuration(300).setInterpolator(OvershootInterpolator(10.0f)).start()

        val fabCloseAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_close)
        createNewLayout.startAnimation(fabCloseAnimation)
        randomChallengeLayout.startAnimation(fabCloseAnimation)
        listChallengesLayout.startAnimation(fabCloseAnimation)

        fab1.isClickable = false
        fab2.isClickable = false
        fab3.isClickable = false
        isFabMenuOpen = false
    }

}
