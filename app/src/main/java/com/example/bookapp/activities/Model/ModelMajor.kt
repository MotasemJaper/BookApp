package com.example.bookapp.activities.Model

class ModelMajor {

    var id: String = ""
    var collegeId: String = ""
    var collegeName: String = ""
    var majorName: String = ""
    var timestamp: String = ""
    var uid: String = ""

    constructor()
    constructor(id: String, collegeId: String, majorName: String, timestamp: String, uid: String, collegeName: String) {
        this.id = id
        this.collegeId = collegeId
        this.majorName = majorName
        this.timestamp = timestamp
        this.uid = uid
        this.collegeName = collegeName
    }

}