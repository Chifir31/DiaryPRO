package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


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
        AMLAZYTOLOGIN()

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
                    if (pwd_field.text.toString().length >= 5) {
                        val preferences = getSharedPreferences("my_prefs", MODE_PRIVATE)
                        val editor = preferences.edit()
                        editor.putBoolean("isLoggedIn", true)
                        editor.apply()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
//                        firebaseAuth.signInWithEmailAndPassword(
//                            email_field.text.toString(),
//                            pwd_field.text.toString()
//                        ).addOnCompleteListener {
//                            if (it.isSuccessful) {
//                                val intent = Intent(this, MainActivity::class.java)
//                                startActivity(intent)
//                            } else {
//                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG)
//                                    .show()
//                            }
//                        }
                    }else{
                        pwd_field.setError("Слишком короткий пароль")
                    }
                }
                else {
                    email_field.setError("Некорректный email")
                }
            }
        }
    }
    fun AMLAZYTOLOGIN(){
        email_field.setText("a@gmail.com")
        pwd_field.setText("123456")
    }
}