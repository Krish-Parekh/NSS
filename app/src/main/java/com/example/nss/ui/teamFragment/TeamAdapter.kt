package com.example.nss.ui.teamFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nss.R
import com.example.nss.model.User
import de.hdodenhof.circleimageview.CircleImageView

class TeamAdapter(val context: Context, private val usersList: ArrayList<User>) :
    RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {
    inner class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val teamImage: CircleImageView = itemView.findViewById<CircleImageView>(R.id.teamImage)
        val userName: TextView = itemView.findViewById<TextView>(R.id.tvUsername)
        val userPos: TextView = itemView.findViewById<TextView>(R.id.tvPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.team_layout, parent, false)
        return TeamViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.apply {
            Glide.with(context).load(usersList[position].imageUrl).into(teamImage)
            userName.text = usersList[position].name
            if (usersList[position].core == true) {
                userPos.text = "Core Member"
            } else {
                userPos.text = "NSS Member"
            }
        }
    }

    override fun getItemCount(): Int = usersList.size
}