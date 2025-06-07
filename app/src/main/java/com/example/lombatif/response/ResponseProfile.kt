package com.example.lombatif.response


import com.example.lombatif.models.get.Profile
import com.google.gson.annotations.SerializedName

data class ResponseProfile(
    @SerializedName("user") val profile : Profile
)
