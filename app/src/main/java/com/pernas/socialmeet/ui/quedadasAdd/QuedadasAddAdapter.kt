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
    var selectedUsers = arrayListOf<String>()
    var selectedUsersId = arrayListOf<String>()
    var hash= hashMapOf<Any,Any>()

    fun showUsers(users: HashMap<Any,Any>) {
       this.hash = users
        for (key in hash) {
            Log.e("AVER ",key.key.toString())
            Log.e("AVER2 ",key.value.toString())
        }
        notifyDataSetChanged()
    }
    fun addSelectedUsers() : ArrayList<String> {
        Log.d("check",selectedUsers.size.toString())
        return selectedUsers
    }
    fun addQuedadasOtherUsers() : ArrayList<String>{
        Log.d("check2",selectedUsersId.size.toString())
        return selectedUsersId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder.from(parent)
    }

    override fun getItemCount(): Int = hash.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var keyList = ArrayList(hash.keys)
        holder.bind(keyList[position].toString(),(hash[keyList[position]] as String),selectedUsers,selectedUsersId)
        addSelectedUsers()
        addQuedadasOtherUsers()
        //holder.bind(usuarios[position],selectedUsers)

    }

    class ViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {
        val quedadasTitle = view.findViewById<TextView>(R.id.userAddtextView)
        var cb = view.findViewById(R.id.cb) as CheckBox



        fun bind(id: String    ,pos: String, selected: ArrayList<String>,selectedId: ArrayList<String>) {

                quedadasTitle.text = pos
                cb.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selected.add(pos)
                        Log.e("el id " ,id)
                        selectedId.add(id)
                    }else {
                        selected.clear()
                        Log.e("NOT CLICKED " ," NO check LIcked")
                    }



            }








            /*quedadasTitle.text = username
            cb.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    test.add(username)
                }else {
                    Log.e("NOT CLICKED " ," NO check LIcked")
                }
            }*/
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.username_item, parent, false)
                return ViewHolder(view)
            }
        }
    }
}