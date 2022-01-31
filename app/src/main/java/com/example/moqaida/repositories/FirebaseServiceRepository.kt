package com.example.moqaida.repositories

import android.net.Uri
import android.util.Log
import com.example.moqaida.model.Items
import com.example.moqaida.model.Requests
import com.example.moqaida.model.Users
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await


private const val USER= "users"
const val SHARED_PREF_FILE = "Auth"
const val USER_ID = "userId"
const val USER_EMAIL = "userEmail"
const val USER_PHONE = "userPhone"
const val USER_NAME = "userFullName"
const val IMAGE_NAME = "imageName"

const val EMAIL = "email"

private const val ITEM = "items"
private const val REQUESTS = "requests"

private const val TAG = "FirebaseServiceReposito"
class FirebaseServiceRepository {

    val  firebaseAuth = FirebaseAuth.getInstance()

    private val db = FirebaseFirestore.getInstance()
    private val imageRef = Firebase.storage.reference


    // Collections
    private val userCollection = db.collection(USER)
    private val itemInfoCollection = db.collection(ITEM)

    private val requestCollection = db.collection(USER)


    //-------------------------------------------------------------------------------------------------------//


    // Sign up
    fun signUp(email: String, password: String) = firebaseAuth.createUserWithEmailAndPassword(email, password)

    // Insert a user into users collection
    fun insertUser(user: Users) =userCollection.document(firebaseAuth.currentUser!!.uid).set(user)

    // upload Item Image to fireStorage
    fun uploadItemImage(imageUri: Uri,filename:String)= imageRef.child("images/$filename").putFile(imageUri)

    // delete image
    fun deleteImage(filename:String) = imageRef.child("images/$filename").delete()

    // upload Item Info to fireStore
    fun uploadItemInfo(item: Items)= itemInfoCollection.document().set(item)

   // Login
    fun login(email: String, password: String)= firebaseAuth.signInWithEmailAndPassword(email,password)

    // retrieve user info
     fun retrieveUserInfo()=userCollection.document(firebaseAuth.currentUser!!.uid)

    // retrieve Items
    suspend fun  retrieveItems() =  itemInfoCollection.whereNotEqualTo(USER_ID,
        firebaseAuth.currentUser?.uid
    ).get().await()

    // retrieve My Items
    suspend fun  retrieveMyItems() =  itemInfoCollection.whereEqualTo(USER_ID,firebaseAuth.currentUser!!.uid).get().await()

    // Insert Bartering Request into Request collection
    fun sendBarteringRequest(request: Requests) = requestCollection.document(request.item!!.userId).collection(REQUESTS).document().set(request)

    // retrieve Bartering Request
     suspend fun retrieveBarteringRequest() = requestCollection.document(firebaseAuth.currentUser!!.uid).collection(REQUESTS).get().await()

    // delete Bartering Request
    fun deleteBarteringRequest(request: Requests) = requestCollection.document(firebaseAuth.currentUser!!.uid).collection(REQUESTS).document(request.requestID).delete()

    // update item
     fun  updateItem(item: Items): Task<Void> { Log.d(TAG,item.documentId)
       return itemInfoCollection.document(item.documentId).set(item,
        SetOptions.merge())

     }

    // delete current user item
    fun deleteMyItem(item: Items) = itemInfoCollection.document(item.documentId).delete()


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