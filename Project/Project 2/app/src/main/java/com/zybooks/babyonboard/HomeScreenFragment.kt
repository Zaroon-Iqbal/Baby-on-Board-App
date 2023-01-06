package com.zybooks.babyonboard

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentTransaction

class HomeScreenFragment : Fragment() {
    private val CHANNEL_ID = "channel_id_example_01"
    private val notificationId = 101

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home_screen, container, false)
        val button = view.findViewById<Button>(R.id.yesBtn)
        button.setOnClickListener(yesBtnListener)
        createNotificationChannel()
        val button1 = view.findViewById<Button>(R.id.noBtn)
        button1.setOnClickListener(noBtnListener)


        return view
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "There is a baby on Board"
            val descriptionText = "Remember to take your Baby with You!"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager= requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }
    private fun sendNotification(){
        val builder = NotificationCompat.Builder(requireContext(),CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_bob_2_xxxhdpi)
            .setContentTitle("Baby On Board")
            .setContentText("You have a Baby with you, drive safely!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
       with(NotificationManagerCompat.from(requireContext())){
           notify(notificationId, builder.build())
       }

    }



    private val yesBtnListener = View.OnClickListener {
        sendNotification()
        val methodSelectionFrag = MethodSelectionFragment()
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.nav_host_fragment, methodSelectionFrag)
            .commit()
    }
    private val noBtnListener = View.OnClickListener {
        val noFragment = NoFragment()
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.nav_host_fragment, noFragment)
            .commit()
    }

}