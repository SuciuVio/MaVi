package com.mavi

import android.os.Bundle
import android.view.View
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

        setSupportActionBar(binding.toolbar)

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
        intent.extras?.let {
            if (it.getBoolean("open_call_screen", false)) {
                val callId = it.getString("call_id") ?: return@let
                val callerName = it.getString("caller_name") ?: "Unknown"
                val callerId = it.getInt("caller_id", 0)

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
