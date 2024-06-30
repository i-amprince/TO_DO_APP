package com.example.learningggg

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.finalapp.R
import com.example.finalapp.UpdateNoteActivity
import java.text.SimpleDateFormat
import java.util.Locale

class NotesAdapter(private var notes: List<Note>, context: Context): RecyclerView.Adapter<NotesAdapter.NoteViewHolder>(){

    private val db: NotesDatabaseHelper = NotesDatabaseHelper(context)

    class NoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val dueDateTextView: TextView = itemView.findViewById(R.id.dueDateTextView)
        val priorityTextView: TextView = itemView.findViewById(R.id.priorityTextView)
        val completedTextView: TextView = itemView.findViewById(R.id.completedTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int  = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content
        holder.dueDateTextView.text = if (note.dueDate!= null) SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(note.dueDate) else ""
        holder.priorityTextView.text = note.priority.name
        holder.completedTextView.text = if (note.completed) "Completed" else "Not Completed"

        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateNoteActivity:: class.java).apply{
                putExtra("note_id", note.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            db.deleteNote(note.id)
            refreshData(db.getAllNotes())
            Toast.makeText(holder.itemView.context, "Task Deleted", Toast.LENGTH_SHORT).show()
        }
    }

    fun refreshData(newNotes: List<Note>){
        notes = newNotes
        notifyDataSetChanged()
    }
}