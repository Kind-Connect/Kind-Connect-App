package com.example.kindconnectapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.FileOutputStream
import java.io.File
import java.util.Calendar

class MyPantryFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()   // Firestore reference
    private val pantryItems = mutableListOf<PantryItem>()
    private lateinit var pantryAdapter: PantryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addItemButton: Button

    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            tempImageView?.setImageURI(uri)
        }
    }

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri?.let { uri ->
                tempImageView?.setImageURI(uri)
            }
        }
    }

    // Temporary reference to the dialog's ImageView
    private var tempImageView: ImageView? = null
    private fun createImageUri(): Uri {
        val image = File(requireContext().filesDir, "pantry_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            image
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_pantry, container, false)

        recyclerView = view.findViewById(R.id.pantryRecyclerView)
        addItemButton = view.findViewById(R.id.addItemButton)
        pantryAdapter = PantryAdapter(pantryItems) { item ->
            removeItem(item)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = pantryAdapter

        addItemButton.setOnClickListener {
            showAddItemDialog()
        }

        loadItems()
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.topToolbar)

        // Set a back arrow icon
        toolbar.setNavigationIcon(R.drawable.outline_arrow_back_24)

        // Make the arrow white
        toolbar.navigationIcon?.setTint(Color.WHITE)

        // Back behavior
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }
    private fun showAddItemDialog() {
        selectedImageUri = null
        val dialogView = layoutInflater.inflate(R.layout.add_pantry_item, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.editName)
        val descInput = dialogView.findViewById<EditText>(R.id.editDescription)
        val quantityInput = dialogView.findViewById<EditText>(R.id.editQuantity)
        val dateInput = dialogView.findViewById<EditText>(R.id.editExpirationDate)
        val imageView = dialogView.findViewById<ImageView>(R.id.itemImageView)
        val uploadButton = dialogView.findViewById<Button>(R.id.uploadImageButton)
        val takePhotoButton = dialogView.findViewById<Button>(R.id.takePhotoButton)

        tempImageView = imageView

        uploadButton.setOnClickListener {
            pickImage.launch("image/*")
        }

        takePhotoButton.setOnClickListener {
            val uri = createImageUri()
            selectedImageUri = uri
            takePhoto.launch(uri)
        }

        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formatted = "${selectedMonth + 1}/$selectedDay/$selectedYear"
                    dateInput.setText(formatted)
                },
                year, month, day
            )

            datePicker.datePicker.minDate = System.currentTimeMillis()
            datePicker.show()
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add Pantry Item")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val localPath = selectedImageUri?.let { saveImageLocally(it) }

                saveItemToFirestore(
                    nameInput.text.toString(),
                    descInput.text.toString(),
                    quantityInput.text.toString().toIntOrNull() ?: 1,
                    dateInput.text.toString(),
                    localPath // this is now the imageUrl field
                )

            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun loadItems() {
        db.collection("pantryItems")
            .get()
            .addOnSuccessListener { result ->
                pantryItems.clear()
                for (doc in result) {
                    val item = PantryItem(
                        doc.getString("name") ?: "",
                        doc.getString("description") ?: "",
                        doc.getLong("quantity")?.toInt() ?: 0,
                        doc.getString("expiration") ?: "",
                        doc.getString("imageUrl"),
                        firestoreId = doc.id
                    )
                    pantryItems.add(item)
                }
                pantryAdapter.notifyDataSetChanged()
            }
    }
    private fun removeItem(item: PantryItem) {
        item.firestoreId?.let { id ->
            db.collection("pantryItems").document(id)
                .delete()
                .addOnSuccessListener {
                    pantryItems.remove(item)
                    pantryAdapter.notifyDataSetChanged()
                    Toast.makeText(context, "Item removed!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error removing item", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun saveImageLocally(uri: Uri): String {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val fileName = "pantry_${System.currentTimeMillis()}.jpg"
        val file = File(requireContext().filesDir, fileName)

        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()

        return file.absolutePath
    }
    private fun saveItemToFirestore(
        name: String,
        description: String,
        quantity: Int,
        expiration: String,
        imagePath: String?
    ) {
        val item = hashMapOf(
            "name" to name,
            "description" to description,
            "quantity" to quantity,
            "expiration" to expiration,
            "imageUrl" to imagePath
        )

        db.collection("pantryItems")
            .add(item)
            .addOnSuccessListener {
                loadItems()
                Toast.makeText(requireContext(), "Item added!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error saving item", Toast.LENGTH_SHORT).show()
            }
    }
}