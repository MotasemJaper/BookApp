package com.example.bookapp.activities.Model

class ModelCollege {
    var id: String = ""
    var college: String = ""
    var imageUri: String = ""
    var timestamp: String = ""
    var uid: String = ""

    constructor()
    constructor(id: String, college: String, timestamp: String, uid: String,imageUri: String) {
        this.id = id
        this.college = college
        this.timestamp = timestamp
        this.uid = uid
        this.imageUri = imageUri
    }

}