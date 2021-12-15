package com.example.moqaida.repositories

import android.net.Uri
import com.example.moqaida.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


private const val USER_COLLECTION= "users"
const val SHARED_PREF_FILE = "Auth"
const val USER_ID = "userId"

class FirebaseServiceRepository {

    private val db = FirebaseFirestore.getInstance()
     val  firebaseAuth = FirebaseAuth.getInstance()
    val imageRef = Firebase.storage.reference


    // Collections
    private val userCollection = db.collection(USER_COLLECTION)


    // Sign up
    fun signUp(email: String, password: String) = firebaseAuth.createUserWithEmailAndPassword(email, password)

    // Insert a user into users collection
    fun insertUser(userId: String, user: Users) =userCollection.document(userId).set(user)

    // upload Item Image to fire store
    fun uploadItemImage(imageUri: Uri,filename:String)= imageRef.child("images/$filename").putFile(imageUri)

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