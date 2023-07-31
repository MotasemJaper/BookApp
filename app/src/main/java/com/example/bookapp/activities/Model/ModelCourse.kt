package com.example.bookapp.activities.Model

class ModelCourse {
    var id: String = ""
    var level: String = ""
    var majorId: String = ""
    var courseName: String = ""
    var majorName: String = ""
    var timestamp:String = ""
    var uid: String = ""

    constructor()
    constructor(
        id: String,
        level: String,
        majorId: String,
        courseName: String,
        timestamp: String,
        uid: String,
        majorName: String
    ) {
        this.id = id
        this.level = level
        this.majorId = majorId
        this.courseName = courseName
        this.timestamp = timestamp
        this.uid = uid
        this.majorName = majorName
    }

}