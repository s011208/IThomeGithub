package yhh.ithome.repository.github.entity

import com.google.gson.annotations.SerializedName

data class UserEntity(
    @field:[SerializedName("login")]
    val login: String = "",
    @field:[SerializedName("id")]
    val id: String = "",
    @field:[SerializedName("name")]
    val name: String = ""
)