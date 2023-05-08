package com.example.myapplication.data

data class User(
    var LastName: String? = null,
    var FirstName: String? = null,
    var login: String? = null,
    var BirthDate: String? = null,
    var role: String? = null
)

data class  Sportsmen(
    var LastName: String? = null,
    var FirstName: String? = null,
    var login: String? = null,
    var BirthDate: String? = null,
    var role: String? = null,
    var CSS: String? = null
)

data class Coach(
    var LastName: String? = null,
    var FirstName: String? = null,
    var login: String? = null,
    var BirthDate: String? = null,
    var role: String? = null,
    var list_of_sportsmen: ArrayList<String>
)