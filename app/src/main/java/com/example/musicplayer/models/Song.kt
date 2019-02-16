package com.example.musicplayer.models

import android.R.id

//Modelo Song para la aplicacion
class Song (var nombre:String,var duracion : String,var artista: String,var  imagen :Int, var id: Long){
    fun getID(): Long {
        return this.id
    }
}