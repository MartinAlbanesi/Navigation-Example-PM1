package com.maralbadev.pm1_example_navxml

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.button_open_detail).setOnClickListener {
            val action = HomeFragmentDirections
                .actionHomeFragmentToDetailFragment(getString(R.string.detail_item_name))
            findNavController().navigate(action)
        }
    }
}
