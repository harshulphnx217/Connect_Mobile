package com.connect.connect

data class Comment(
    var userId: String,
    var commentId: String,
    var postId: String,
    var commentDateTime: String,
    var commentMessage: String,
    var userProfilePicLink: String
)
