package com.example.moqaida.repositories

import com.example.moqaida.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


private const val USER_COLLECTION= "users"

class FirebaseServiceRepository {

    private val db = FirebaseFirestore.getInstance()
    private val  firebaseAuth = FirebaseAuth.getInstance()

    // Collections
    private val userCollection = db.collection(USER_COLLECTION)


    // Sign up
    fun signUp(email: String, password: String) = firebaseAuth.createUserWithEmailAndPassword(email, password)

    // Insert a user into users collection
    fun insertUser(userId: String, user: Users) =userCollection.document(userId).set(user)

   // Login
    fun login(email: String, password: String)= firebaseAuth.signInWithEmailAndPassword(email,password)

    // This companion object is to makes our Firebase Service a singleton
    companion object {
        private var instance: FirebaseServiceRepository? = null

        fun init() {
            if (instance == null) {
                instance = FirebaseServiceRepository()
            }
        }

        fun get(): FirebaseServiceRepository {
            return instance ?: throw Exception("Firebase service repository must be initialized")
        }
    }


}