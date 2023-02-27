package com.example.myapplication

import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class RegisterActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var psw: EditText
        lateinit var psw_repeat: EditText
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val users: Map<String, String>

        val btn_complete_register = findViewById(R.id.RegisterCompleteBtn) as Button
        psw = findViewById(R.id.Psw)
        psw_repeat = findViewById(R.id.RepeatPsw)

        btn_complete_register.setOnClickListener {
            var psw_string = psw.text.toString()
            var psw_repeat_sting = psw_repeat.text.toString()

            if (psw_string != psw_repeat_sting) {
                Toast.makeText(this, "не совпадают пароли!", Toast.LENGTH_LONG).show()

            }

        }
    }
}