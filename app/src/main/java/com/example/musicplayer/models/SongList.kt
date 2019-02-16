package com.example.musicplayer.models

interface SongList {

    val songlist: ArrayList<Song> // Contactos

    fun add(element: Song) // Agregar elemento

}