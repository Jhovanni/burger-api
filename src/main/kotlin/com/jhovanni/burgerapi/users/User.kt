package com.jhovanni.burgerapi.users

import java.util.UUID

data class User(val id: UUID, val email: String, val roles: List<String>)
