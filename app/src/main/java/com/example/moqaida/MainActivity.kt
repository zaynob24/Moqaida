package com.example.moqaida

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.moqaida.databinding.ActivityMainBinding
import com.example.moqaida.repositories.FirebaseServiceRepository


//Navigation Drawer with Fragments - Navigation Component
//https://www.youtube.com/watch?v=PvuaPL4D-N8&ab_channel=CodeWithMazn

class MainActivity : AppCompatActivity() {


    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseServiceRepository.init() // init for the Repository then we use it any where


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        navController = findNavController(R.id.fragmentContainerView)
        binding.navigationView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(navController.graph,binding.drawerLayout)

        setupActionBarWithNavController(navController,appBarConfiguration)

    }


    override fun onSupportNavigateUp(): Boolean {
        val  navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // so when user in home page and click back press ,,app close
    override fun onBackPressed() {
        when(navController.currentDestination?.id) {
            R.id.homeFragment -> finish()
            R.id.enterPhoneFragment -> navController.navigate(R.id.action_enterPhoneFragment_to_loginFragment)
            else -> super.onBackPressed()
        }
    }
}