package com.project.lifeos.viewmodel

import com.project.lifeos.interactor.TestInteractor

class HomeViewModel(private val interactor: TestInteractor) {
    fun title(): String {
        return interactor.test()
    }
}