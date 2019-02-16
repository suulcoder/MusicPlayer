package com.example.musicplayer

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ListView
import android.widget.MediaController
import android.widget.Toast
import com.example.musicplayer.adapter.SongAdapter
import com.example.musicplayer.models.Song
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.MediaController.MediaPlayerControl
import com.example.musicplayer.models.Extension
import android.content.ComponentName
import com.example.musicplayer.MusicService.MusicBinder
import android.os.IBinder
import android.content.ServiceConnection


/*
* Algunos metodos fueron tomados de:
* https://code.tutsplus.com/tutorials/create-a-music-player-on-android-project-setup--mobile-22764
* https://code.tutsplus.com/tutorials/create-a-music-player-on-android-user-controls--mobile-22787
* */

class MainActivity : AppCompatActivity(), MediaPlayerControl{

    private var controller: MusicController? = null
    private var musicService = MusicService()
    private var playIntent : Intent? = null
    private var musicBound : Boolean = false
    private var paused = false
    var playbackPaused = false


    companion object {
        val PERMISSION_REQUEST_CODE = 12
    }

    override fun onStart() {//OnStart override
        super.onStart()
        if (playIntent == null) {
            playIntent = Intent(this, MusicService::class.java)
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE)
            startService(playIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val songPosn = 0
        //Pedimos permisos
        if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            //Si aun no los conceden
            ActivityCompat.requestPermissions(this@MainActivity,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE)
        }
        else{//si los concedieron accederemos a la base de datos
            tomardatos()
            setController()
        }


        //SI el usuairo selecciona de la lista
        lista.setOnItemClickListener { parent, view, position, id ->//redirigimos a la plantilla dle contacto
            musicService.setSong(position)
            musicService.playSong()
            if (playbackPaused) {
                setController()
                playbackPaused = false
            }
            controller!!.show(0)
        }
    }

    //connect to the service
    val musicConnection : ServiceConnection = object: ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            musicBound = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder : MusicBinder = service as MusicBinder
            musicService = binder.service
            musicService.setList(Extension.songlist)
            musicBound = true
        }

    }

    private fun tomardatos() {//Toma los datos y los guarda en songData
        var songCursor = contentResolver.query(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,null,null,null)


        if (songCursor!=null &&  songCursor.moveToFirst()){
            do{
                val thisId = songCursor.getLong(songCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID))
                val Name = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val Duracion = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val artista = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                Extension.songlist.add(Song(Name,Duracion,artista,R.drawable.logo,thisId))
                }while (songCursor.moveToNext())

        }
        var listview = findViewById(R.id.lista) as ListView
        val adapter = SongAdapter(applicationContext, Extension.songlist)//nuestro adapter personalizado
        listview.setAdapter(adapter)//lo agregamos a nuestro list view

    }

    override fun onStop() {
        controller!!.hide()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        if (paused) {
            setController()
            paused = false
        }
    }

    override fun onPause() {
        super.onPause()
        paused = true
    }

    //Pide permiso
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext,"Permiso Concedido",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setController() {//Setiamos al controlador
        controller = MusicController(this);
        controller!!.setPrevNextListeners(View.OnClickListener { playNext() },View.OnClickListener { playPrev() })
        controller!!.setMediaPlayer(this);
        controller!!.setAnchorView(findViewById(R.id.lista));
        controller!!.setEnabled(true);
    }

    //play next
    private fun playNext() {//Avanzar
        musicService.playNext()
        if(playbackPaused){
            setController()
            playbackPaused = false
        }
        controller!!.show(0)
    }

    //play previous
    private fun playPrev() {//Retroceder
        musicService.playPrev()
        if(playbackPaused){
            setController()
            playbackPaused = false
        }
        controller!!.show(0)
    }


    /*Metodos sobre escritos de la interfaz*/

    override fun isPlaying(): Boolean {
        if(musicService!=null && musicBound){
            return musicService.isPng()
        }
        return false
    }

    override fun canSeekForward(): Boolean {
        return true
    }

    override fun getDuration(): Int {
        if(musicService!=null && musicBound && musicService.isPng()){
            return musicService.getDur()
        }
        else return 0

    }

    override fun pause() {
        playbackPaused = true
        musicService.pausePlayer()
    }

    override fun getBufferPercentage(): Int {
        return 50
    }

    override fun seekTo(pos: Int) {
        musicService.seek(pos)
    }

    override fun getCurrentPosition(): Int {
        if(musicService!=null && musicBound && musicService.isPng()){
            return musicService.getPosn()
        }
        else{
            return 0
        }
    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun start() {
        musicService.go()
    }

    override fun getAudioSessionId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canPause(): Boolean {
        return true
    }

    override fun onDestroy() {
        stopService(playIntent)
        super.onDestroy()
    }
}
