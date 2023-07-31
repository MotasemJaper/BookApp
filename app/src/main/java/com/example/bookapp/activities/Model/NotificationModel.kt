package com.example.bookapp.activities.Model

class NotificationModel {
    var id: String = ""
    var nameCollege: String = ""
    var name: String = ""

    constructor()
    constructor(id: String, nameCollege: String, name: String) {
        this.id = id
        this.nameCollege = nameCollege
        this.name = nameCollege
    }
}