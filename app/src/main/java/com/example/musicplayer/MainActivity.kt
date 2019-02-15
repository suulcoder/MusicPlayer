package com.example.musicplayer

import android.Manifest
import android.content.ContentResolver
import android.content.Context
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
import com.example.musicplayer.MainActivity.MusicController





class MainActivity : AppCompatActivity(), MediaPlayerControl {

    private val controller: MusicController? = null
    var songData: ArrayList<Song> = ArrayList()//Se guardaran todas las canciones en este ArrayList
    val player:MediaPlayer = null
    companion object {
        val PERMISSION_REQUEST_CODE = 12
    }


    override fun isPlaying(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canSeekForward(): Boolean {
        return true
    }

    override fun getDuration(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pause() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun seekTo(pos: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCurrentPosition(): Int {

    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canPause(): Boolean {
        return true
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val songPosn = 0
        initMusicPlayer()
        //Pedimos permisos
        if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            //Si aun no los conceden
            ActivityCompat.requestPermissions(this@MainActivity,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE)
        }
        else{//si los concedieron accederemos a la base de datos
            tomardatos()
            setController()//setiamos el controlador
        }
    }

    fun initMusicPlayer() {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private fun tomardatos() {//Toma los datos y los guarda en songData
        var songCursor = contentResolver.query(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,null,null,null)


        if (songCursor!=null &&  songCursor.moveToFirst()){
            do{
                val Name = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val Duracion = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val artista = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                songData.add(Song(Name,Duracion,artista,R.drawable.logo))
            }while (songCursor.moveToNext())

        }
        var listview = findViewById(R.id.lista) as ListView
        val adapter = SongAdapter(applicationContext, songData)//nuestro adapter personalizado
        listview.setAdapter(adapter)//lo agregamos a nuestro list view


    }


    //Pide permiso
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext,"Permiso Concedido",Toast.LENGTH_LONG).show()
            }
        }
    }

    inner class MusicController(c: Context) : MediaController(c) {//Musci controller para la multimmedia

        override fun hide() {}

    }

    private fun setController() {
        //setiamos el controller
        val controller = MusicController(this)
        controller.setPrevNextListeners(object : View.OnClickListener() {
            override fun onClick(v: View) {
                playNext()//SI el usuario desea avanzar
            }
        }, object : View.OnClickListener() {
            override fun onClick(v: View) {
                playPrev()//Si el usuario desea retroceder
            }
        })
        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.lista);
        controller.isEnabled = true;
    }

    //play next
    private fun playNext() {
        player.playNext()
        controller.show(0)
    }

    //play previous
    private fun playPrev() {
        player.playPrev()
        controller.show(0)
    }

    //NO utilizaremos los siguientes metodos

    override fun getBufferPercentage(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAudioSessionId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
