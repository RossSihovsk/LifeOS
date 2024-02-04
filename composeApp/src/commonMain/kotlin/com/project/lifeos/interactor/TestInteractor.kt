package com.project.lifeos.interactor

import com.project.lifeos.repository.TestRepository

class TestInteractor(private val repository: TestRepository) {
    fun test(): String {
        return repository.test()
    }
}