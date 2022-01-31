package com.example.moqaida.model

class Requests(

    val itemNameMassage: String = "",
    val itemDescriptionMassage: String = "",

    val user: Users? = null,  // user who send the request
    val item: Items? = null,

    var requestID: String =""

    )