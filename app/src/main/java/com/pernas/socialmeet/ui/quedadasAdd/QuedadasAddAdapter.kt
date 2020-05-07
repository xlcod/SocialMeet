package com.pernas.socialmeet.ui.quedadasAdd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pernas.socialmeet.R


class QuedadasAddAdapter(private val listener: (List<Any?>) -> Unit) : RecyclerView.Adapter<QuedadasAddAdapter.ViewHolder>() {


    private var usuarios = ArrayList<String>()

    fun showUsers(users: ArrayList<String>) {
       this.usuarios = users
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder.from(parent)
    }

    override fun getItemCount(): Int = usuarios.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(usuarios[position],listener)
    }

    class ViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {
        val quedadasTitle = view.findViewById<TextView>(R.id.userAddtextView)



        fun bind(username: String, listener: ( List<Any?>) -> Unit) {

        quedadasTitle.text = username

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.username_item, parent, false)
                return ViewHolder(view)
            }
        }
    }


}