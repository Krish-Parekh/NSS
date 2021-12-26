package com.example.nss.ui.homeFragment

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.nss.R
import com.example.nss.model.Activity
import com.example.nss.model.User
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

interface RvButton {
    fun onDeleteClick(creationTime: String, position: Int);
    fun onEditClick(creationTime: String, bundle: Activity);
    fun enrolClick(bundle: Activity)
}

class ActivityAdapter(
    val context: Context,
    private val activityList: ArrayList<Activity>,
    private val listener: HomeFragment,
    private val core: Boolean
) :
    RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirebaseDatabase: DatabaseReference


    inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.tvEventName)
        val eventIdentity: TextView = itemView.findViewById(R.id.tvEventIdentity)
        val eventDate: TextView = itemView.findViewById(R.id.tvDate)
        val eventTime: TextView = itemView.findViewById(R.id.tvTime)
        val deleteBtn: ImageView = itemView.findViewById(R.id.deleteBtn)
        val editBtn: ImageView = itemView.findViewById(R.id.editBtn)
        val teamCard: CardView = itemView.findViewById(R.id.teamCard)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_layout, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {

        holder.apply {
            eventName.text = activityList[position].activityName
            eventIdentity.text = activityList[position].activityIdentity
            eventDate.text = activityList[position].activityDate
            eventTime.text = activityList[position].activityTime

            if (core) {
                deleteBtn.visibility = View.VISIBLE
                editBtn.visibility = View.VISIBLE
            } else {
                deleteBtn.visibility = View.INVISIBLE
                editBtn.visibility = View.INVISIBLE
            }
            val bundle = Activity(
                activityList[position].activityName,
                activityList[position].activityIdentity,
                activityList[position].activityDescription,
                activityList[position].activityDate,
                activityList[position].activityTime,
                activityList[position].link,
                activityList[position].creation_time_ms
            )

            deleteBtn.setOnClickListener {
                listener.onDeleteClick(activityList[position].creation_time_ms!!, position)
            }
            editBtn.setOnClickListener {
                listener.onEditClick(activityList[position].creation_time_ms!!, bundle)
            }
            teamCard.setOnClickListener {
                listener.enrolClick(bundle)
            }
        }
    }

    override fun getItemCount(): Int = activityList.size


}