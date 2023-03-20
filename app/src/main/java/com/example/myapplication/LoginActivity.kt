package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.text.TextUtils
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var email_field: EditText
    private lateinit var pwd_field : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btn_register = findViewById(R.id.RegisterBtn) as Button
        val btn_login: Button = findViewById(R.id.LoginBtn)
        email_field = findViewById(R.id.Email_field)
        pwd_field = findViewById(R.id.Pwd_field)


        btn_register.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        firebaseAuth = FirebaseAuth.getInstance()

        btn_login.setOnClickListener{
            validateEmptyForm()
        }
    }
    private fun validateEmptyForm() {
        firebaseAuth = FirebaseAuth.getInstance()
        when {
            TextUtils.isEmpty(email_field.text.toString().trim()) -> email_field.setError("Пожалуйста введите логин")
            TextUtils.isEmpty(pwd_field.text.toString().trim()) -> pwd_field.setError("Пожалуйста введите пароль")

            email_field.text.toString().isNotEmpty() && pwd_field.text.toString().isNotEmpty() -> {
                if (email_field.text.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
//                    firebaseAuth.signInWithEmailAndPassword(email_field.text.toString(), pwd_field.text.toString()).addOnCompleteListener {
//                        if (it.isSuccessful) {
//                            val intent = Intent(this, MainActivity::class.java)
//                            startActivity(intent)
//                        } else {
//                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
//                        }
//                    }
                }
                else {
                    email_field.setError("Некорректный email")
                }
            }
        }
    }
}