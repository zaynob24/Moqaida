package com.example.moqaida

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.moqaida.databinding.ActivityMainBinding
import com.example.moqaida.views.MainActivity2

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        binding.signUpButton.setOnClickListener {
//
//            val intent = Intent(this,MainActivity2::class.java)
//            startActivity(intent)
//            finish()
//        }

        //requireContext()
        val items = listOf("Option 1", "Option 2", "Option 3", "Option 4")
        val adapter = ArrayAdapter(this, R.layout.list_item_years, items)
        (binding.usedForMenu.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        (binding.locationMenu.editText as? AutoCompleteTextView)?.setAdapter(adapter)



    }
}