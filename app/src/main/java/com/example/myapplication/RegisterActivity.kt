package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast


class RegisterActivity: AppCompatActivity() {
    private lateinit var username: EditText
    private lateinit var pwd: EditText
    private lateinit var c_pwd: EditText
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val users: Map<String, String>

        val btn_complete_register = findViewById(R.id.RegisterCompleteBtn) as Button
        username = findViewById(R.id.username)
        pwd = findViewById(R.id.Pwd)
        c_pwd = findViewById(R.id.RepeatPwd)

        btn_complete_register.setOnClickListener {
            var psw_string = pwd.text.toString()
            var psw_repeat_sting = c_pwd.text.toString()

            validateEmptyForm()
            }

        }
    private fun validateEmptyForm() {
        when{
            TextUtils.isEmpty(username.text.toString().trim())->username.setError("Пожалуйста введите логин")
            TextUtils.isEmpty(pwd.text.toString().trim())->pwd.setError("Пожалуйста введите пароль")
            TextUtils.isEmpty(c_pwd.text.toString().trim())->c_pwd.setError("Пожалуйста подтвердите пароль")

            username.text.toString().isNotEmpty() && pwd.text.toString().isNotEmpty() && c_pwd.text.toString().isNotEmpty() ->
            {
                if(username.text.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))){
                    if(pwd.text.toString().length>=5) {
                        if(pwd.text.toString() == c_pwd.text.toString()) {
                            Toast.makeText(this, "Успех", Toast.LENGTH_LONG).show()
                        }
                        else{
                            c_pwd.setError("Не совпадают пароли")
                        }
                    }
                    else{
                        pwd.setError("Слишком короткий пароль")
                    }
                }
                else{
                    username.setError("Некорректный адрес электронной почты")
                }
            }
        }
    }
}