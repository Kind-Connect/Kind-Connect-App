package com.example.kindconnectapp

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class RecipePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int) =
        if (position == 0) RecommendedRecipesFragment() else SearchRecipesFragment()
}