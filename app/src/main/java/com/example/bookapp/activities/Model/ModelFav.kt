package com.example.bookapp.activities.Model

class ModelFav {
    var nameBook: String = ""
    var url: String = ""
    var typeMaterial: String = ""
    var courseId: String = ""
    var nameCourse: String = ""

    constructor()
    constructor(
        nameBook: String,
        url: String,
        typeMaterial: String,
        courseId: String,
        nameCourse: String
    ) {
        this.nameBook = nameBook
        this.url = url
        this.typeMaterial = typeMaterial
        this.courseId = courseId
        this.nameCourse = nameCourse
    }

}