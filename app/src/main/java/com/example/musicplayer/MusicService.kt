package com.example.musicplayer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.example.musicplayer.models.Song

class MusicService(var player:MediaPlayer, var songs : ArrayList<Song>, var songPosn:Int) : Service() ,MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener{

    override fun onPrepared(mp: MediaPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCompletion(mp: MediaPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    fun getPosn(): Int {
        return player.getCurrentPosition()//devuelve la posicion actual
    }

    fun getDur(): Int {
        return player.getDuration()//devuelve la duracion
    }

    fun isPng(): Boolean {
        return player.isPlaying()//devuelve el estado de reproduccion
    }

    fun pausePlayer() {
        player.pause()
    }

    fun seek(posn: Int) {
        player.seekTo(posn)
    }

    fun go() {
        player.start()
    }

    fun playPrev(){
        songPosn--;
        if(this.songPosn&lt;0){
            songPosn=songs.size()-1
        };
        playSong();
    }

    fun playNext(){
        songPosn++;
        if(songPosn&gt;=songs.size()){
            songPosn=0
        }
        playSong();
    }

}
