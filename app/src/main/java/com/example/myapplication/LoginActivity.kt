package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.ArrayMap
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson


class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var email_field: EditText
    private lateinit var pwd_field : EditText
    private lateinit var database: DatabaseReference
    lateinit var role: String
    lateinit var profileList: ArrayMap<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btn_register = findViewById(R.id.RegisterBtn) as Button
        val btn_login: Button = findViewById(R.id.LoginBtn)
        email_field = findViewById(R.id.Email_field)
        pwd_field = findViewById(R.id.Pwd_field)
        AMLAZYTOLOGIN(true)

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
                        //val intent = Intent(this, MainActivity::class.java)
                        //startActivity(intent)
                        firebaseAuth.signInWithEmailAndPassword(
                            email_field.text.toString(),
                            pwd_field.text.toString()
                        ).addOnCompleteListener {
                            if (it.isSuccessful) {
                                database = Firebase.database.reference
                                var user = ""
                                Log.d("check", email_field.text.split("@")[0])
                                database.child("users").child(email_field.text.split("@")[0]).get().addOnSuccessListener {
                                    if (it.exists()) {
                                        user = it.child("role").value.toString()
                                        if (user == "C") {
                                            profileList = ArrayMap <String, String>().apply{
                                                put("lastName", it.child("lastName").value.toString())
                                                put("firstName", it.child("firstName").value.toString())
                                                put("birthDate", it.child("birthDate").value.toString())
                                                put("role", it.child("role").value.toString())
                                                put("login", it.child("login").value.toString())
                                                put("id", email_field.text.split("@")[0])
                                            }
                                        }
                                        else if (user == "S"){
                                            profileList = ArrayMap <String, String>().apply{
                                                put("lastName", it.child("lastName").value.toString())
                                                put("firstName", it.child("firstName").value.toString())
                                                put("birthDate", it.child("birthDate").value.toString())
                                                put("role", it.child("role").value.toString())
                                                put("login", it.child("login").value.toString())
                                                put("id", email_field.text.split("@")[0])
                                                put("weight", it.child("weight").value.toString())
                                                put("height", it.child("height").value.toString())
                                                put("css", it.child("css").value.toString())
                                                put("coach", it.child("coach").value.toString())
                                            }
                                        }
                                        editor.putString("profileList", Gson().toJson(profileList))
                                        editor.apply()
                                    }
                                }
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
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
    fun AMLAZYTOLOGIN(boolean: Boolean){
        if(boolean){
            email_field.setText("abc@gmail.com")
            pwd_field.setText("123456")}
        else {
            email_field.setText("def@gmail.com")
            pwd_field.setText("654321")
        }
    }
}