package com.example.musicplayer.adapter

import android.content.Context
import android.support.v7.view.menu.MenuView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.musicplayer.R
import com.example.musicplayer.models.Song
import kotlinx.android.synthetic.main.music.view.*

class  SongAdapter(var context: Context, var songlist:ArrayList<Song>): BaseAdapter() {

    private class ViewHolder(row: View?){
        var txtName: TextView
        var duracion: TextView
        var artista: TextView
        var ivImage: ImageView

        init {
            this.txtName = row?.findViewById(R.id.name) as TextView
            this.duracion = row?.findViewById(R.id.duracion) as TextView
            this.artista = row?.findViewById(R.id.artista) as TextView
            this.ivImage = row?.findViewById(R.id.photo) as ImageView
        }
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View?
        var viewHolder: ViewHolder
        if (convertView==null){
            var layout = LayoutInflater.from(context)
            view = layout.inflate(R.layout.music,convertView,false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }
        else{
            view = convertView
            viewHolder = view.tag as ViewHolder
        }



        var song:Song = getItem(position) as Song

        viewHolder.txtName.text = song.nombre
        viewHolder.duracion.text = song.duracion
        viewHolder.artista.text = song.artista
        viewHolder.ivImage.setImageResource(song.imagen)



        return view as View


    }

    override fun getItem(position: Int): Any {
        return songlist.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    override fun getCount(): Int {
        return songlist.count()
    }
}