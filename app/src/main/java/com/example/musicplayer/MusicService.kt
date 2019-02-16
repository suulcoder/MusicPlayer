package com.example.musicplayer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.widget.Toast
import com.example.musicplayer.models.Extension
import com.example.musicplayer.models.Song
import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentUris
import android.media.AudioManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.PowerManager
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.util.Log
import com.example.musicplayer.MusicService.MusicBinder





class MusicService() : Service(),MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener{

    //MusicService para poder reproducir en playback

    var mp:MediaPlayer? = null
    private val NOTIFY_ID = 1
    var songPosn:Int = 0
    var songs:ArrayList<Song> = ArrayList()
    private val musicBind = MusicBinder()

    override fun onCreate() {//ON create override
        super.onCreate()
        songPosn=0
        mp = MediaPlayer()
        initMusicPlayer();
    }

    fun setList(theSongs: ArrayList<Song>) {//Setiamos la lista
        songs = theSongs
    }

    inner class MusicBinder : Binder() {//BInder
        internal val service: MusicService
            get() = this@MusicService
    }

    override fun onUnbind(intent: Intent): Boolean {
        mp!!.stop()
        mp!!.release()
        return false
    }

    fun initMusicPlayer() {
        mp!!.setWakeMode(getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK)
        mp!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mp!!.setOnPreparedListener(this);
        mp!!.setOnCompletionListener(this);
        mp!!.setOnErrorListener(this);
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onPrepared(player: MediaPlayer?) {//Metodo de interfaz OnPrepared
        player!!.start()
        var notIntent = Intent(this, MainActivity::class.java)
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        var pendInt = PendingIntent.getActivity(
            this, 0,
            notIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        var builder = Notification.Builder(this)

        builder.setContentIntent(pendInt)
            .setSmallIcon(R.drawable.logo)
            .setTicker(songs[songPosn].nombre)
            .setOngoing(true)
            .setContentTitle("Playing")
            .setContentText(songs[songPosn].nombre)
        var not = builder.build()

        startForeground(NOTIFY_ID,not)
    }

    override fun onError(player: MediaPlayer?, what: Int, extra: Int): Boolean {//Metodo onError
        player!!.reset()
        return false
    }

    override fun onCompletion(player: MediaPlayer?) {//Metodo onCompletion
        if(mp!!.getCurrentPosition()==0){
            player!!.reset()
            playNext()
        }
    }

    override fun onBind(intent: Intent): IBinder? {//OnBInd
        return musicBind
    }

    fun playSong() {//Reproducir cancion
        mp!!.reset()
        var playSong = this.songs[this.songPosn];
        var currrSong = playSong.getID()
        var trackUri : Uri = ContentUris.withAppendedId(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,currrSong)
        try {
            mp!!.setDataSource(applicationContext, trackUri)
            mp!!.prepareAsync();
        }
        catch (e: Exception) {
            Log.e("MUSIC SERVICE", "Error setting data source", e)
        }

    }

    override fun onDestroy() {
        stopForeground(true)
    }


    /*Metodos sobre escritos de interfaz*/

    fun playPrev(){
        songPosn=songPosn-1
        if(songPosn<0){
            songPosn=(songs.size)-1
        }
        playSong()
    }

    fun playNext(){
        songPosn=songPosn+1
        if(songPosn==songs.size){
            songPosn=0
        }
        playSong()
    }

    fun setSong(songIndex: Int) {
        songPosn = songIndex
    }

    fun getPosn(): Int {
        return mp!!.getCurrentPosition()
    }

    fun getDur(): Int {
        return mp!!.getDuration()
    }

    fun isPng(): Boolean {
        return mp!!.isPlaying()
    }

    fun pausePlayer() {
        mp!!.pause()
    }

    fun seek(posn: Int) {
        mp!!.seekTo(posn)
    }

    fun go() {
        mp!!.start()
    }



}
