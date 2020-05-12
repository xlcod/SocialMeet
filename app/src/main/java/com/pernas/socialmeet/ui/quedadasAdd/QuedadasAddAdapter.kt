package com.pernas.socialmeet.ui.quedadasAdd

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pernas.socialmeet.R


class QuedadasAddAdapter(private val listener: (List<Any?>) -> Unit) : RecyclerView.Adapter<QuedadasAddAdapter.ViewHolder>() {


    private var usuarios = ArrayList<String>()
    var test = arrayListOf<String>()

    fun showUsers(users: ArrayList<String>) {
       this.usuarios = users
        notifyDataSetChanged()
    }
    fun checkUsers() : ArrayList<String> {
        Log.d("check",test.size.toString())
        return test
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder.from(parent)
    }

    override fun getItemCount(): Int = usuarios.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(usuarios[position],test)
        checkUsers()
    }

    class ViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {
        val quedadasTitle = view.findViewById<TextView>(R.id.userAddtextView)
        var cb = view.findViewById(R.id.cb) as CheckBox



        fun bind(username: String, test: ArrayList<String>) {

            quedadasTitle.text = username
            cb.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    test.add(username)
                }else {
                    Log.e("NOT CLICKED " ," NO check LIcked")
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.username_item, parent, false)
                return ViewHolder(view)
            }
        }
    }
}