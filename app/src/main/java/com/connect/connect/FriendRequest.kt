package com.connect.connect

data class FriendRequest(
    var friendId: String,
    var userId: String,
    var requestDate: String,
    var requestStatus: String
)
