package com.example.learningggg

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*

class NotesDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "notesapp.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "allnotes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        private const val COLUMN_DUE_DATE = "dueDate"
        private const val COLUMN_PRIORITY = "priority"
        private const val COLUMN_COMPLETED = "completed"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery =
            "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, " +
                    "$COLUMN_CONTENT TEXT, $COLUMN_DUE_DATE TEXT, $COLUMN_PRIORITY TEXT, " +
                    "$COLUMN_COMPLETED INTEGER)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertNote(note: Note) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
            put(COLUMN_DUE_DATE, note.dueDate?.let { dateToString(it) })
            put(COLUMN_PRIORITY, note.priority.name)
            put(COLUMN_COMPLETED, if (note.completed) 1 else 0)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllNotes(): List<Note> {
        val notesList = mutableListOf<Note>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
            val dueDateStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE))
            val dueDate = if (dueDateStr != null) stringToDate(dueDateStr) else null
            val priorityStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY))
            val priority = Priority.valueOf(priorityStr)
            val completedInt = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED))
            val completed = completedInt == 1

            val note = Note(id, title, content, dueDate, priority, completed)
            notesList.add(note)
        }
        cursor.close()
        db.close()
        return notesList
    }

    private fun dateToString(date: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }

    private fun stringToDate(dateStr: String): Date {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.parse(dateStr) ?: Date()
    }

    fun updateNote(note: Note){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
            put(COLUMN_DUE_DATE, note.dueDate?.let { dateToString(it) })
            put(COLUMN_PRIORITY, note.priority.name)
            put(COLUMN_COMPLETED, if (note.completed) 1 else 0)
        }

        val whereClause = "$COLUMN_ID =?"
        val whereArgs = arrayOf(note.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun getNoteByID(noteId: Int): Note{
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $noteId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
        val dueDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE))?.let { stringToDate(it) }
        val priority = Priority.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY)))
        val completed = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1

        cursor.close()
        db.close()

        return Note(id, title, content, dueDate, priority, completed)
    }

    fun deleteNote(noteId: Int){
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(noteId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }
}
