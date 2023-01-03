package com.example.picoff.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.picoff.MainViewModel
import com.example.picoff.R
import com.example.picoff.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkIfLoggedInWithGoogle()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                println("YEAH " +viewModel.isFabMenuOpen.value)
                if (viewModel.isFabMenuOpen.value == true)
                    viewModel.isFabMenuOpen.value = false
                else
                    finish()
            }
        })

        viewModel.jumpToChallengeList.observe(this) {
            binding.navView.selectedItemId = R.id.navigation_challenges
        }
    }

    private fun checkIfLoggedInWithGoogle() {
        auth = FirebaseAuth.getInstance()
        // If not signed in open SignInActivity
        if (auth.currentUser == null) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }



}
