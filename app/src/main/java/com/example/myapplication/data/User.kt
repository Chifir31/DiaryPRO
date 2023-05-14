package com.example.myapplication.data

data class User(
    var LastName: String? = null,
    var FirstName: String? = null,
    var login: String? = null,
    var BirthDate: String? = null,
    var role: String? = null
)

data class  Sportsmen(
    var lastName: String? = null,
    var firstName: String? = null,
    var login: String? = null,
    var birthDate: String? = null,
    var role: String? = null,
    var height: String? = null,
    var weight: String? = null,
    var css: String? = null,
    var coach: String? = null
)

data class Coach(
    var lastName: String? = null,
    var firstName: String? = null,
    var login: String? = null,
    var birthDate: String? = null,
    var role: String? = null,
    var list_of_sportsmen: List<String>
)