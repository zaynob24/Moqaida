package com.example.moqaida.util

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RegisterValidationsTest{
    private lateinit var validator:RegisterValidations

    @Before  //@Before test
    //so before every test the function will execute
    fun setup(){
        validator = RegisterValidations()
    }

    @Test
    fun emailIsValidWithInvalidEmailThenReturnFalseValue(){

        // "test-dd.com" -->> false data
        val validation = validator.emailIsValid("test-dd.com")
        // here I except false and i pass the validation
        assertEquals(false,validation)
    }

    @Test
    fun emailIsValidWithValidEmailThenReturnTrueValue(){

        // "test@test.com" -->> true data
        val validation = validator.emailIsValid("test@test.com")
        // here I except true and i pass the validation
        assertEquals(true,validation)
    }

    @Test
    fun passwordIsValidWithInvalidPasswordThenReturnFalseValue(){

        val validation = validator.passwordIsValid("73")
        assertEquals(false,validation)
    }

    @Test
    fun passwordIsValidWithValidPasswordThenReturnTrueValue(){

        val validation = validator.passwordIsValid("Tu@212312")
        assertEquals(true,validation)
    }
}