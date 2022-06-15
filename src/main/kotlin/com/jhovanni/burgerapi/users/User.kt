package com.jhovanni.burgerapi.users

data class User(val email: String, val password: String, val roles: List<String>)
