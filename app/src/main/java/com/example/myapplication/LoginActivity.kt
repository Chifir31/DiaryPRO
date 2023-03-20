package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btn_register = findViewById(R.id.RegisterBtn) as Button
        val btn_login: Button = findViewById(R.id.LoginBtn)
        val email_field = findViewById(R.id.Email_field) as EditText
        val pwd_field = findViewById(R.id.Pwd_field) as EditText


        btn_register.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        firebaseAuth = FirebaseAuth.getInstance()

        btn_login.setOnClickListener{
            if(email_field.text.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")))
            {
                firebaseAuth.signInWithEmailAndPassword(email_field.text.toString(),pwd_field.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful)
                    {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
    }
}