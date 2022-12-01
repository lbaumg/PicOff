package com.example.challenge

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.challenge.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private var isFabMenuOpen = false

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        supportActionBar?.hide()


        val baseFloatingActionButton = findViewById<View>(R.id.baseFloatingActionButton)
        baseFloatingActionButton.setOnClickListener {
            if (!isFabMenuOpen) expandFabMenu() else collapseFabMenu()
        }
    }

    private fun expandFabMenu() {
        val baseFloatingActionButton = findViewById<View>(R.id.baseFloatingActionButton)
        val randomChallengeLayout = findViewById<View>(R.id.randomChallengeLayout)
        val createNewLayout = findViewById<View>(R.id.createNewLayout)
        val listChallengesLayout = findViewById<View>(R.id.listChallengesLayout)
        ViewCompat.animate(baseFloatingActionButton).rotation(45.0f).withLayer()
            .setDuration(300).setInterpolator(OvershootInterpolator(10.0f)).start()

        val fabOpenAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_open)
        randomChallengeLayout.startAnimation(fabOpenAnimation)
        createNewLayout.startAnimation(fabOpenAnimation)
        listChallengesLayout.startAnimation(fabOpenAnimation)

        findViewById<View>(R.id.createNewFab).isClickable = true
        findViewById<View>(R.id.randomChallengeFab).isClickable = true
        findViewById<View>(R.id.listChallengesFab).isClickable = true

        isFabMenuOpen = true
    }

    private fun collapseFabMenu() {
        val baseFloatingActionButton = findViewById<View>(R.id.baseFloatingActionButton)
        val randomChallengeLayout = findViewById<View>(R.id.randomChallengeLayout)
        val createNewLayout = findViewById<View>(R.id.createNewLayout)
        val listChallengesLayout = findViewById<View>(R.id.listChallengesLayout)
        ViewCompat.animate(baseFloatingActionButton).rotation(0.0f).withLayer()
            .setDuration(300).setInterpolator(OvershootInterpolator(10.0f)).start()



        val fabCloseAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_close)
        createNewLayout.startAnimation(fabCloseAnimation)
        randomChallengeLayout.startAnimation(fabCloseAnimation)
        listChallengesLayout.startAnimation(fabCloseAnimation)

        findViewById<View>(R.id.createNewFab).isClickable = false
        findViewById<View>(R.id.randomChallengeFab).isClickable = false
        findViewById<View>(R.id.listChallengesFab).isClickable = false
        isFabMenuOpen = false
    }

    override fun onBackPressed() {
        if (isFabMenuOpen)
            collapseFabMenu()
        else
            super.onBackPressed()
    }
}
