package com.jhovanni.burgerapi.auth

import java.util.*

data class UserCredentials(val id: UUID, val roles: List<String>)
