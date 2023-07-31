package com.example.bookapp.activities.Model

class UserDashBord {
    var name: String = ""
    var email: String = ""
    var profileImage: String = ""
    var userType : String = ""
    var Uid: String = ""
    var unId: String = ""
    var gpa: String = ""
    var id: String = ""

    constructor()
    constructor(name: String, email: String, profileImage: String, userType: String, Uid: String,gpa: String,unId: String, id:String) {
        this.name = name
        this.email = email
        this.profileImage = profileImage
        this.userType = userType
        this.Uid = Uid
        this.unId = unId
        this.gpa = gpa
        this.id = id
    }
}