package com.example.picoff.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.picoff.R
import com.example.picoff.databinding.ActivityMainBinding
import com.example.picoff.receivers.RemindersManager
import com.example.picoff.viewmodels.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    companion object {
        const val ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 100
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
    private lateinit var auth: FirebaseAuth

    private val viewModel: MainViewModel by viewModels {
        SavedStateViewModelFactory(application, this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        val deniedPermissions = checkPermissions()
        if (deniedPermissions.isNotEmpty()) {
            requestDeniedPermissions(deniedPermissions)
        }

        checkIfLoggedInWithGoogle()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        viewModel.jumpToChallengeList.observe(this) {
            binding.navView.selectedItemId = R.id.navigation_challenges
        }

        viewModel.bottomNavigationVisibility.observe(this, Observer { navVisibility ->
            navVisibility?.let {
                navView.visibility = it
            }
        })

        createNotificationsChannels()
        RemindersManager.startReminder(this)

        listenForInternetConnectivity()
    }


    private fun createNotificationsChannels() {
        val channel = NotificationChannel(
            getString(R.string.reminders_notification_channel_id),
            getString(R.string.reminders_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        ContextCompat.getSystemService(this, NotificationManager::class.java)
            ?.createNotificationChannel(channel)
    }


    // Return an array of the denied permissions
    fun checkPermissions() : ArrayList<String>{
        val permissions = arrayListOf<String>(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.RECEIVE_BOOT_COMPLETED
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(
                android.Manifest.permission.SCHEDULE_EXACT_ALARM
            )
        }

        val deniedPermissions = arrayListOf<String>()

        for (permission in permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission)
            }
        }
        return deniedPermissions
    }

    private fun requestDeniedPermissions(deniedPermissions: ArrayList<String>)  {
        requestPermissions(deniedPermissions.toTypedArray(), ASK_MULTIPLE_PERMISSION_REQUEST_CODE)
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

        if (intent?.type == "text/plain") {
            val data = intent?.extras?.getString(Intent.EXTRA_TEXT)?.substringAfter("\"")?.substringBefore("\"")
            viewModel.sharedUserName.value = data
            navView.selectedItemId = R.id.navigation_friends
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterListenerForInternetConnectivity()
    }


}
