package com.example.bookapp.activities.Model

class ModelPdfViewForBook {

    var bookId: String = ""
    var courseId: String = ""
    var description: String = ""
    var downloadsCount: Long = 0
    var id: String = ""
    var majorId: String = ""
    var nameBook: String = ""
    var nameCourse: String = ""
    var nameMajor: String = ""
    var timestamp: String = ""
    var typeMaterial: String = ""
    var uid: String = ""
    var url: String = ""
    var viewsCount: Long =  0

    constructor()
    constructor(
        bookId: String,
        courseId: String,
        description: String,
        downloadsCount: Long,
        id: String,
        majorId: String,
        nameBook: String,
        nameCourse: String,
        nameMajor: String,
        timestamp: String,
        typeMaterial: String,
        uid: String,
        url: String,
        viewsCount: Long
    ) {
        this.bookId = bookId
        this.courseId = courseId
        this.description = description
        this.downloadsCount = downloadsCount
        this.id = id
        this.majorId = majorId
        this.nameBook = nameBook
        this.nameCourse = nameCourse
        this.nameMajor = nameMajor
        this.timestamp = timestamp
        this.typeMaterial = typeMaterial
        this.uid = uid
        this.url = url
        this.viewsCount = viewsCount
    }


}