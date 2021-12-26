package com.example.nss.ui.enrollFragment

import android.content.Context
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nss.R
import com.example.nss.model.User
import de.hdodenhof.circleimageview.CircleImageView

interface BtnRole {
    fun assignTask(position: Int)
    fun deleteUser(uid: String, name: String)
}

class EnrolAdapter(
    val context: Context,
    private val enrolUser: ArrayList<User>,
    val core: Boolean,
    private val listener: EnrolFragment
) :
    RecyclerView.Adapter<EnrolAdapter.EnrolViewHolder>() {

    inner class EnrolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImage: CircleImageView = itemView.findViewById(R.id.userImage)
        val username: TextView = itemView.findViewById(R.id.tvUsername)
        val assignTask: ImageView = itemView.findViewById(R.id.assignTask)
        val deleteUser: ImageView = itemView.findViewById(R.id.deleteUser)
        val taskPosition: TextView = itemView.findViewById(R.id.tvPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnrolViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.enrol_layout, parent, false)
        return EnrolViewHolder(view)
    }

    override fun onBindViewHolder(holder: EnrolViewHolder, position: Int) {
        holder.apply {
            Glide.with(context).load(enrolUser[position].imageUrl).into(userImage)
            username.text = enrolUser[position].name
            if (!core) {
                assignTask.visibility = View.INVISIBLE
                deleteUser.visibility = View.INVISIBLE
            }

            // Email Photo and Email Report Logic
            taskPosition.visibility = View.INVISIBLE
            if (enrolUser[position].Ephoto == true) {
                taskPosition.visibility = View.VISIBLE
                taskPosition.text = "Email Photo"
            } else if (enrolUser[position].Ereport == true) {
                taskPosition.visibility = View.VISIBLE
                taskPosition.text = "Email Report"
            }

            deleteUser.setOnClickListener {
                listener.deleteUser(enrolUser[position].uid!!, enrolUser[position].name!!)
            }

            assignTask.setOnClickListener {
                listener.assignTask(position)
            }
        }
    }

    override fun getItemCount(): Int = enrolUser.size
}