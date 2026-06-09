package org.example.project.login

import org.example.project.login.LoginViewModel
import org.koin.mp.KoinPlatform

object ViewModelFactory {
    fun loginViewModel(): LoginViewModel =
        KoinPlatform.getKoin().get()
}