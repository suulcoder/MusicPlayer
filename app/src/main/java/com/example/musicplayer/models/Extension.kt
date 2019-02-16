package com.example.musicplayer.models

import android.app.Application

class Extension : Application() {

    companion object:SongList{

        override val songlist: ArrayList<Song> =  ArrayList()
        var posicion = 0

        override fun add(element: Song){
           songlist.add(element)
        }

    }

}