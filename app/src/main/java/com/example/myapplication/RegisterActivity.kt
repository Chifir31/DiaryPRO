package com.example.myapplication

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.app.DatePickerDialog
import android.content.Intent
import android.util.Log
import android.widget.*
import com.google.android.material.textfield.TextInputLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class RegisterActivity: AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var lastname: EditText
    private lateinit var firstname: EditText
    private lateinit var editdate: EditText
    private lateinit var username: EditText
    private lateinit var pwd: EditText
    private lateinit var c_pwd: EditText
    private lateinit var role1: CheckBox
    private lateinit var role2: CheckBox

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register1)

        val users: Map<String, String>

        val btn_complete_register = findViewById(R.id.RegisterCompleteBtn) as Button
        lastname = findViewById(R.id.lname)
        firstname = findViewById(R.id.fname)
        editdate = findViewById(R.id.editdate)
        username = findViewById(R.id.username)
        pwd = findViewById(R.id.Pwd)
        c_pwd = findViewById(R.id.RepeatPwd)
        role1 = findViewById(R.id.role1)
        role2 = findViewById(R.id.role2)

        btn_complete_register.setOnClickListener {
            validateEmptyForm()
            }

        role1.setOnClickListener {
            onCheckboxClicked(true)
            role1.setError(null)
        }
        role2.setOnClickListener {
            onCheckboxClicked(false)
            role2.setError(null)
        }

        val c: Calendar
        c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        editdate.setOnClickListener{
            DatePickerDialog(this, this, year, month, day).show()
            editdate.setError(null)
        }
        }
    private fun validateEmptyForm() {
        firebaseAuth = FirebaseAuth.getInstance()
        when{
            TextUtils.isEmpty(lastname.text.toString().trim())->lastname.setError("Пожалуйста введите фамилию")
            TextUtils.isEmpty(firstname.text.toString().trim())->firstname.setError("Пожалуйста введите имя")
            (!(role1.isChecked) && !(role2.isChecked))->role1.setError("Пожалуйста выберите роль")
            TextUtils.isEmpty(editdate.text.toString().trim())->editdate.setError("Пожалуйста введите дату рождения")
            TextUtils.isEmpty(username.text.toString().trim())->username.setError("Пожалуйста введите логин")
            TextUtils.isEmpty(pwd.text.toString().trim())->pwd.setError("Пожалуйста введите пароль")
            TextUtils.isEmpty(c_pwd.text.toString().trim())->c_pwd.setError("Пожалуйста подтвердите пароль")

            lastname.text.toString().isNotEmpty() && firstname.text.toString().isNotEmpty() && (role1.isChecked || role2.isChecked) && editdate.text.toString().isNotEmpty() && username.text.toString().isNotEmpty() &&  pwd.text.toString().isNotEmpty() && c_pwd.text.toString().isNotEmpty() -> {
                if (lastname.text.toString().matches(Regex("(^[А-Яа-яЁё]{1,25})"))) {
                    if (firstname.text.toString().matches(Regex("(^[А-Яа-яЁё]{1,25})"))) {
                        if (username.text.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                            if (pwd.text.toString().length >= 5) {
                                if (pwd.text.toString() == c_pwd.text.toString()) {
                                    Toast.makeText(this, "Успех", Toast.LENGTH_LONG).show()
                                    firebaseAuth.createUserWithEmailAndPassword(username.text.toString(),
                                    pwd.text.toString()).addOnCompleteListener{
                                        if(it.isSuccessful){
                                            val intent = Intent(this,MainActivity::class.java)
                                            startActivity(intent)
                                        }else{
                                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } else {
                                    c_pwd.setError("Не совпадают пароли")
                                }
                            } else {
                                pwd.setError("Слишком короткий пароль")
                            }
                        } else {
                            username.setError("Некорректный адрес электронной почты")
                        }
                    } else {
                        firstname.setError("Неккоректное имя")
                    }
                } else {
                    lastname.setError("Неккоректная фамилия")
                }
            }
        }
    }

    private fun onCheckboxClicked(flag: Boolean) {
        if(flag)
            role2.isChecked = false
        else
            role1.isChecked = false
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        editdate.setText("$year/$month/$dayOfMonth")
    }
}