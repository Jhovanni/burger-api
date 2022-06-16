package com.jhovanni.burgerapi.users

import java.util.UUID

data class User(val uuid: UUID, val email: String, val password: String, val roles: List<String>)
