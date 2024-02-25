package com.example.newzbreak.common

sealed class NewsResource<T>(var data:T ?= null, var message:String ?= null) {

    class Success<T>(data: T) : NewsResource<T>(data = data)
    class Error<T>(message:String, data: T?=null) : NewsResource<T>(data, message)
    class Loading<T> : NewsResource<T>()

}