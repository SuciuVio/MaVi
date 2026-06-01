package com.mavi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.mavi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Setup AppBar with Navigation
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.loginFragment,
                R.id.chatListFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Handle incoming call from notification
        handleIncomingCall(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingCall(intent)
    }

    private fun handleIncomingCall(intent: Intent?) {
        if (intent?.getBooleanExtra("open_call_screen", false) == true) {
            val callId = intent.getStringExtra("call_id")
            val callerName = intent.getStringExtra("caller_name")
            val callerId = intent.getIntExtra("caller_id", 0)

            if (callId != null) {
                val bundle = Bundle().apply {
                    putString("call_id", callId)
                    putString("caller_name", callerName)
                    putInt("caller_id", callerId)
                }
                navController.navigate(R.id.callFragment, bundle)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
