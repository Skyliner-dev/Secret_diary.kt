package org.hyperskill.secretdiary

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

const val PREFERENCES_NAME = "PREF_DIARY"
const val PREFERENCES_KEY  = "KEY_DIARY_TEXT"
class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.btnSave)
        val textF = findViewById<TextView>(R.id.tvDiary)
        val eText = findViewById<EditText>(R.id.etNewWriting)
        val uButton = findViewById<Button>(R.id.btnUndo)
        sharedPreferences = getSharedPreferences(
            PREFERENCES_NAME, Context.MODE_PRIVATE)
        textF.text = sharedPreferences.getString(PREFERENCES_KEY, "")
        val editor = sharedPreferences.edit()
        button.setOnClickListener {
            if (eText.text.trim().isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Empty or blank input cannot be saved",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val time = dateFormat.format(Clock.System.now().toEpochMilliseconds())
                val text = "$time\n${eText.text}\n\n" + textF.text
                textF.text = text.trim()
                editor.putString("KEY_DIARY_TEXT", text.trim()).apply()
                eText.text.clear()
            }
        }

        uButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Remove last note")
                .setMessage("Do you really want to remove the last writing? This operation cannot be undone!")
                .setPositiveButton("Yes") { _, _ ->

                    val text = textF.text.toString()
                    if (!text.contains("\n\n")) textF.text = ""
                    else {
                        textF.text = text.substringAfter("\n\n").trim()
                        editor.putString("KEY_DIARY_TEXT", textF.text.toString()).apply()
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }
    }
}