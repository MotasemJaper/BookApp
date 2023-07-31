package com.example.bookapp.activities.Model

class ModelBook {
    var id: String = ""
    var courseId: String = ""
    var bookName: String = ""
    var bookDescription: String = ""
    var timestamp: String = ""
    var uid: String = ""

    constructor()
    constructor(
        id: String,
        courseId: String,
        bookName: String,
        bookDescription: String,
        timestamp: String,
        uid: String
    ) {
        this.id = id
        this.courseId = courseId
        this.bookName = bookName
        this.bookDescription = bookDescription
        this.timestamp = timestamp
        this.uid = uid
    }

}