package com.example.bookapp.activities.Model

class Message {
    var name: String = ""
    var email: String = ""
    var Uid: String = ""
    var id: String = ""
    var timestamp: String = ""
    var message: String = ""
    var url: String = ""

    constructor()
    constructor(
        name: String,
        email: String,
        Uid: String,
        id: String,
        timestamp: String,
        message: String,
        url: String
    ) {
        this.name = name
        this.email = email
        this.Uid = Uid
        this.id = id
        this.timestamp = timestamp
        this.message = message
        this.url = url
    }


}