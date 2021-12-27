package com.example.moqaida.repositories

import android.content.SharedPreferences
import android.net.Uri
import com.example.moqaida.model.Items
import com.example.moqaida.model.Requests
import com.example.moqaida.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await


private const val USER= "users"
const val SHARED_PREF_FILE = "Auth"
const val USER_ID = "userId"
const val USER_EMAIL = "userEmail"
const val USER_PHONE = "userPhone"
const val USER_NAME = "userFullName"

const val EMAIL = "email"

private const val ITEM = "items"
private const val REQUESTS = "requests"


class FirebaseServiceRepository {

    val  firebaseAuth = FirebaseAuth.getInstance()

    private val db = FirebaseFirestore.getInstance()
    private val imageRef = Firebase.storage.reference


    // Collections
    private val userCollection = db.collection(USER)
    private val itemInfoCollection = db.collection(ITEM)

    private val requestCollection = db.collection(USER).document(firebaseAuth.currentUser!!.uid).collection(REQUESTS)


    //-------------------------------------------------------------------------------------------------------//


    // Sign up
    fun signUp(email: String, password: String) = firebaseAuth.createUserWithEmailAndPassword(email, password)

    // Insert a user into users collection
    fun insertUser(user: Users) =userCollection.document(firebaseAuth.currentUser!!.uid).set(user)

    // upload Item Image to fireStorage
    fun uploadItemImage(imageUri: Uri,filename:String)= imageRef.child("images/$filename").putFile(imageUri)

    // upload Item Info to fireStore
    fun uploadItemInfo(item: Items)= itemInfoCollection.document().set(item)

   // Login
    fun login(email: String, password: String)= firebaseAuth.signInWithEmailAndPassword(email,password)

    // retrieve user info
    suspend fun retrieveUserInfo(email:String)=userCollection.whereEqualTo(EMAIL,email).get().await()

    // retrieve Items
    suspend fun  retrieveItems() =  itemInfoCollection.get().await()


    // Insert Bartering Request into Request collection
    fun sendBarteringRequest(request: Requests) = requestCollection.document().set(request)

    //-------------------------------------------------------------------------------------------------------//

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