package com.example.finalapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalapp.databinding.ActivityUpdateNoteBinding
import com.example.learningggg.Note
import com.example.learningggg.NotesDatabaseHelper
import com.example.learningggg.Priority
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpdateNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateNoteBinding
    private lateinit var db: NotesDatabaseHelper
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityUpdateNoteBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = NotesDatabaseHelper(this)

        noteId = intent.getIntExtra("note_id", -1)
        if (noteId == -1){
            finish()
            return
        }

        val note =  db.getNoteByID(noteId)
        binding.updateTitleEdit.setText(note.title)
        binding.editDescription.setText(note.content)
        binding.editDueDate.setText(note.dueDate?.toString())

        when (note.priority) {
            Priority.LOW-> binding.editPriorityLow.isChecked = true
            Priority.MEDIUM -> binding.editPriorityMedium.isChecked = true
            Priority.HIGH -> binding.editPriorityHigh.isChecked = true
        }

        binding.editCompletionStatus.isChecked = note.completed

        binding.updateSaveButton.setOnClickListener {
            val newTitle = binding.updateTitleEdit.text.toString()
            val newContent = binding.editDescription.text.toString()
            val newDueDate = binding.editDueDate.text.toString().let { if (it.isEmpty()) null else stringToDate(it) }
            val newPriority = when {
                binding.editPriorityLow.isChecked -> Priority.LOW
                binding.editPriorityMedium.isChecked -> Priority.MEDIUM
                binding.editPriorityHigh.isChecked -> Priority.HIGH
                else -> Priority.LOW
            }
            val newCompleted = binding.editCompletionStatus.isChecked

            val updatedNote = Note(noteId, newTitle, newContent, newDueDate, newPriority, newCompleted)

            db.updateNote(updatedNote)
            finish()
            Toast.makeText(this, "CHANGES SAVED", Toast.LENGTH_SHORT).show()
        }
    }

    fun stringToDate(dateString: String): Date? {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        return dateFormat.parse(dateString)
    }
}