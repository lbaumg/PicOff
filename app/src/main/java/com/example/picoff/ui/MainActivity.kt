package com.example.picoff.ui

import android.content.Intent
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.picoff.R
import com.example.picoff.databinding.ActivityMainBinding
import com.example.picoff.viewmodels.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

// TODO Permission handling: ask for camera and storage
// TODO Notificate user when new challenge is coming
// TODO intent to add friend (if intent is detected, navigate to friends screen with name in searchtext)
// TODO detect if offline
// TODO result show for both
// TODO Sign in activity as fragment


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

//        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                if (viewModel.isFabMenuOpen.value == true)
//                    viewModel.isFabMenuOpen.value = false
//                else
//                    finish()
//            }
//        })

        viewModel.jumpToChallengeList.observe(this) {
            binding.navView.selectedItemId = R.id.navigation_challenges
        }

        viewModel.bottomNavigationVisibility.observe(this, Observer { navVisibility ->
            navVisibility?.let {
                navView.visibility = it
            }
        })


        listenForInternetConnectivity()
    }


    private val networkCallback: NetworkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            println("Internet Available")
        }
        override fun onLost(network: Network) {
            Toast.makeText(baseContext, "Device is offline", Toast.LENGTH_SHORT).show()
        }
    }

    private fun listenForInternetConnectivity() {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    private fun unregisterListenerForInternetConnectivity() {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    override fun onResume() {
        super.onResume()
        viewModel.initialize()
    }

    private fun checkIfLoggedInWithGoogle() {
        auth = FirebaseAuth.getInstance()
        // If not signed in open SignInActivity
        if (auth.currentUser == null) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterListenerForInternetConnectivity()
    }


}
