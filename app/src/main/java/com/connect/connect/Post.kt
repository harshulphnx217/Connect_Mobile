package com.connect.connect

data class Post(
    var userName: String,
    var postId: String,
    var postTitle: String,
    var postImgLink: String,
    var postDesc: String,
    var uploadDateTime: String,
    var noOfLikes: String,
    var noOfComments: String,
    var profileImgLink: String
)
