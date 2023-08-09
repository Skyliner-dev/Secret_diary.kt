package org.hyperskill.secretdiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

const val password = "1234"
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginactivity_main)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val etP = findViewById<EditText>(R.id.etPin)
        loginButton.setOnClickListener {
            if (etP.text.toString() == password) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent).also { isFinishing  }
                finish()
            }
            else {
                etP.error = "Wrong PIN!"
            }
        }
    }
}