package com.zybooks.babyonboard

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.zybooks.babyonboard.databinding.ActivityMainBinding

import kotlinx.android.synthetic.main.fragment_bluetooth_page.*
import kotlinx.android.synthetic.main.fragment_bluetooth_page.view.*
import java.util.*
import kotlin.concurrent.timer

class BluetoothPageFragment : Fragment() {

    private val REQUEST_CODE_ENABLE_BT:Int = 1
    private val REQUEST_CODE_DISCOVERABLE_BT:Int = 2
    // lateinit var binding:ActivityMainBinding
    private val CHANNEL_ID = "channel_id_example_02"
    private val notificationId = 102
    private val CHANNEL_ID2 = "channel_id_example_03"
    private val notificationId2 = 103

    //bluetooth adapter
    lateinit var bAdapter: BluetoothAdapter

    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_bluetooth_page, container, false)

        val bluetoothManager= requireActivity().getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bAdapter= bluetoothManager.adapter

        createNotificationChannel()
        createNotificationChannel1()


      //  val stat = view?.findViewById<Button>(R.id.bluetoothStatusTv)
        if(bAdapter==null){

                view.bluetoothStatusTv.text = "Bluetooth is not available"

        } else {

            view.bluetoothStatusTv.text = "Bluetooth is available"

            //set image according to bluetooth status(on/off)
            val iV = view?.findViewById<ImageView>(R.id.bluetoothIv)
            if (bAdapter.isEnabled) {
                //bluetooth is on
                if (iV != null) {
                    iV.setImageResource(R.drawable.ic_bluetooth_off)
                }
            } else {
                //bluetooth is off
                if (iV != null) {
                    iV.setImageResource(R.drawable.ic_bluetooth_on)
                }
            }

            //turn on bluetooth
            val OnBtn = view?.findViewById<Button>(R.id.turnOnBtn)
            if (OnBtn != null) {
                OnBtn.setOnClickListener {
                    if (bAdapter.isEnabled) {
                        sendNotification()
                        Toast.makeText(context, "already on", Toast.LENGTH_LONG).show()

                    } else {
                        //turn on bluetooth
                        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
                        sendNotification()

                    }
                }
            }

            //turn off bluetooth
            val OffBtn = view?.findViewById<Button>(R.id.turnOffBtn)
            if (OffBtn != null) {
                OffBtn.setOnClickListener {
                    if (!bAdapter.isEnabled) {
                        Toast.makeText(context, "already off", Toast.LENGTH_LONG).show()
                        val handler = Handler(Looper.getMainLooper())
                        handler.postDelayed({
                            sendNotification1()
                        },5000)
                    } else {
                        bAdapter.disable()
                        if (iV != null) {
                            iV.setImageResource(R.drawable.ic_bluetooth_off)
                        }
                        Toast.makeText(context, "Bluetooth turned off", Toast.LENGTH_LONG).show()
                        val handler = Handler(Looper.getMainLooper())
                        handler.postDelayed({
                            sendNotification1()
                        },5000)
                    }
                }
            }


            //discoverable the bluetooth
            val discoverable = view?.findViewById<Button>(R.id.discoverableBtn)
            if (discoverable != null) {
                discoverable.setOnClickListener {
                    if (!bAdapter.isDiscovering) {
                        Toast.makeText(
                            context,
                            "Making your device discoverable",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = (Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
                        startActivityForResult(intent, REQUEST_CODE_DISCOVERABLE_BT)
                    }
                }
            }
            //get list of paired devices
            val pair = view?.findViewById<Button>(R.id.pairedBtn)
            if (pair != null) {
                pair.setOnClickListener {
                    if (bAdapter.isEnabled) {
                        val tV = view?.findViewById<Button>(R.id.pairedTv)
                        if (tV != null) {
                            tV.text = "Paired Devices"
                        }
                        val devices = bAdapter.bondedDevices
                        for (device in devices) {
                            val deviceName = device.name
                            val deviceAddress = device
                            if (tV != null) {
                                tV.append("\nDevice: $deviceName, $device")
                            }
                        }
                    } else {
                        Toast.makeText(context, "Turn on bluetooth first", Toast.LENGTH_LONG).show()

                    }
                }
            }
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true ) {
                override fun handleOnBackPressed() {
                    val methodSelectionFrag = MethodSelectionFragment()
                    val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
                    transaction.replace(R.id.nav_host_fragment, methodSelectionFrag)
                        .commit()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            REQUEST_CODE_ENABLE_BT ->
                if(resultCode== Activity.RESULT_OK){
                    val iV = view?.findViewById<ImageView>(R.id.bluetoothIv)
                    if (iV != null) {
                        iV.setImageResource(R.drawable.ic_bluetooth_on)
                    }
                    Toast.makeText(context, "Bluetooth is on", Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(context, "Could not turn on bluetooth", Toast.LENGTH_LONG).show()

                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "There is a baby on Board"
            val descriptionText = "Remember to take your Baby with You!"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }
    private fun createNotificationChannel1(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "There is a baby on Board"
            val descriptionText = "Remember to take your Baby with You!"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID2, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }
    private fun sendNotification(){
        val bitmap = BitmapFactory.decodeResource(requireContext().resources, R.drawable.ic_bob_2_xxxhdpi)

        val builder = NotificationCompat.Builder(requireContext(),CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_bluetooth_on)
            .setLargeIcon(bitmap)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            .setContentTitle("Baby On Board")
            .setContentText("Please drive safely!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(requireContext())){
            notify(notificationId, builder.build())
        }

    }
    private fun sendNotification1(){
        val bitmap = BitmapFactory.decodeResource(requireContext().resources, R.drawable.ic_bob_2_xxxhdpi)

        val builder = NotificationCompat.Builder(requireContext(),CHANNEL_ID2)
            .setSmallIcon(R.drawable.ic_bluetooth_off)
            .setLargeIcon(bitmap)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            .setContentTitle("Baby On Board!!!")
            .setContentText("Please make sure you grab your BABY!!!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(requireContext())){
            notify(notificationId2, builder.build())
        }

    }

}