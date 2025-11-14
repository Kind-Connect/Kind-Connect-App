package com.example.kindconnectapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyPantryFragment : Fragment() {
    private val pantryItems = mutableListOf<PantryItem>()
    private lateinit var pantryAdapter: PantryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addItemButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_pantry, container, false)

        recyclerView = view.findViewById(R.id.pantryRecyclerView)
        addItemButton = view.findViewById(R.id.addItemButton)

        pantryAdapter = PantryAdapter(pantryItems)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = pantryAdapter

        addItemButton.setOnClickListener {
            showAddItemDialog()
        }

        return view
    }

    private fun showAddItemDialog() {
        val dialogView = layoutInflater.inflate(R.layout.add_pantry_item, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.editName)
        val descInput = dialogView.findViewById<EditText>(R.id.editDescription)
        val quantityInput = dialogView.findViewById<EditText>(R.id.editQuantity)
        val dateInput = dialogView.findViewById<EditText>(R.id.editExpirationDate)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Pantry Item")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val item = PantryItem(
                    nameInput.text.toString(),
                    descInput.text.toString(),
                    quantityInput.text.toString().toIntOrNull() ?: 1,
                    dateInput.text.toString()
                )
                pantryItems.add(item)
                pantryAdapter.notifyDataSetChanged()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}