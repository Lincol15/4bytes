package com.example.acopiodeleche

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform