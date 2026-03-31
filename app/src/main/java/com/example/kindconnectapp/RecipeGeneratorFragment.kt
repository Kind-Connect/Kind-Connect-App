package com.example.kindconnectapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class RecipeGeneratorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.recipe_generator_fragment, container, false)
    }

    override fun onViewCreated(view: View, Bundle: Bundle?) {
        super.onViewCreated(view, Bundle)

        val toolbar = view.findViewById<Toolbar>(R.id.topToolbar)
        val tabLayout = view.findViewById<TabLayout>(R.id.recipeTabLayout)
        val viewPager = view.findViewById<ViewPager2>(R.id.recipeViewPager)

        // Back button
        toolbar.setNavigationIcon(R.drawable.outline_arrow_back_24)
        toolbar.navigationIcon?.setTint(Color.WHITE)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Set up ViewPager adapter
        viewPager.adapter = RecipePagerAdapter(this)

        // Connect tabs to ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Recommended" else "Search"
        }.attach()
    }
}