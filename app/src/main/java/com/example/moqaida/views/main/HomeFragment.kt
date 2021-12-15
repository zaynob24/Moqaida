package com.example.moqaida.views.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.moqaida.R


class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //TO make option menu show (search icon)
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // link menu with action bar
        requireActivity().menuInflater.inflate(R.menu.main_menu,menu)    }
}