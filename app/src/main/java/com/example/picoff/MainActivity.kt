package com.example.picoff

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.picoff.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
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

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        lifecycleScope.launch {
            checkIfLoggedInWithGoogle()
        }
    }

    private suspend fun checkIfLoggedInWithGoogle() {
        // Retrieve google account data from preferences datastore if already saved
        mainViewModel.retrieveGoogleAccountData()

        // Wait for preferences data to load
        while (mainViewModel.loadedData.value == false) {
            delay(1)
        }

        // If not signed in open SignInActivity
        if (mainViewModel.isLoggedIn.value == false) {
            val intent = Intent(this, SignInActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    // Opens activity and handles result
    val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null && data.extras?.get("account") != null) {
                    val account = data.extras?.get("account") as GoogleSignInAccount
                    mainViewModel.updateAccount(
                        account.idToken, account.displayName, account.email, account.photoUrl
                    )
                }
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

    override fun onBackPressed() {
        if (isFabMenuOpen)
            collapseFabMenu()
        else
            super.onBackPressed()
    }
}
