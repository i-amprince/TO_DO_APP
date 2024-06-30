package com.example.finalapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalapp.databinding.ActivityAddNoteBinding
import com.example.learningggg.Note
import com.example.learningggg.NotesDatabaseHelper
import com.example.learningggg.Priority
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var db: NotesDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = NotesDatabaseHelper(this)

        binding.saveButton.setOnClickListener {
            val title = binding.titleEdit.text.toString()
            val content = binding.description.text.toString()
            val dueDateStr = binding.dueDateEdit.text.toString()
            val dueDate: Date? = if (dueDateStr.isNotEmpty()) {
                val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                format.parse(dueDateStr)
            } else null

            val priority = when (binding.priorityLow.isChecked) {
                true -> Priority.LOW
                false -> when (binding.priorityMedium.isChecked) {
                    true -> Priority.MEDIUM
                    false -> Priority.HIGH
                }
            }

            val completed = binding.completionStatus.isChecked

            val note = Note(0, title, content, dueDate, priority, completed)
            db.insertNote(note)
            finish()

            Toast.makeText(this, "NOTE SAVED", Toast.LENGTH_SHORT).show()
        }
    }
}