package com.example.moqaida

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.moqaida.databinding.ActivityMainBinding
import com.example.moqaida.views.MainActivity2
import com.example.moqaida.views.idintity.LoginFragment
import com.example.moqaida.views.main.AddItemFragment
import com.example.moqaida.views.main.HomeFragment
import com.example.moqaida.views.main.MyItemFragment

//Navigation Drawer with Fragments - Navigation Component
//https://www.youtube.com/watch?v=PvuaPL4D-N8&ab_channel=CodeWithMazn

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        navController = findNavController(R.id.fragmentContainerView)
        binding.navigationView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(navController.graph,binding.drawerLayout)

        setupActionBarWithNavController(navController,appBarConfiguration)


//        binding.signUpButton.setOnClickListener {
//
//            val intent = Intent(this,MainActivity2::class.java)
//            startActivity(intent)
//            finish()
//        }

//        //requireContext()
//        val items = listOf("Option 1", "Option 2", "Option 3", "Option 4")
//        val adapter = ArrayAdapter(this, R.layout.list_item_years, items)
//        (binding.usedForMenu.editText as? AutoCompleteTextView)?.setAdapter(adapter)
//        (binding.locationMenu.editText as? AutoCompleteTextView)?.setAdapter(adapter)


    }

    override fun onSupportNavigateUp(): Boolean {
        val  navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}