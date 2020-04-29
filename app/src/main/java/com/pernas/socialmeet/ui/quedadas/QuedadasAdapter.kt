package com.pernas.socialmeet.ui.quedadas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.pernas.socialmeet.R
import com.squareup.picasso.Picasso


class QuedadasAdapter(private val listener: ( List<Any?>)  -> Unit) : RecyclerView.Adapter<QuedadasAdapter.ViewHolder>() {


    private var quedadas = HashMap<Any, Any>()


    fun addQuedadas(quedadaLista: HashMap<Any, Any>) {
        this.quedadas = quedadaLista
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var keyList = ArrayList(quedadas.keys)

        holder.bind(quedadas.get(keyList[position])!! as Collection<*>,listener)

    }

    override fun getItemCount(): Int = quedadas.size


    class ViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {
        val quedadasTitle = view.findViewById<TextView>(R.id.titleItem)
        val quedadasDate = view.findViewById<TextView>(R.id.dateItem)
        val imageviewItem = view.findViewById<ImageView>(R.id.quedadasImageViewItem)


        fun bind(pos: Collection<*>, listener: ( List<Any?>) -> Unit) {

            for (key in pos) {
                println("Element at key ${key.toString()}")
                var data = pos.toList()
                var nombre = data[5].toString()
                var fecha = data[0].toString()
                var imageUrl = data[6].toString()
                quedadasTitle.text = nombre
                quedadasDate.text = fecha
                Picasso.get().load(imageUrl).into(imageviewItem)
                this.itemView.setOnClickListener {
                    listener.invoke(data)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
                return ViewHolder(view)
            }
        }
    }
}