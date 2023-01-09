package com.zybooks.babyonboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction


class MethodSelectionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_method_selection, container, false)

        // This callback will only be called when the fragment is at least started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    val homeScreenFrag = HomeScreenFragment()
                    val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
                    transaction.replace(R.id.nav_host_fragment, homeScreenFrag)
                        .commit()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        // The callback can be enabled or disabled here or in handleOnBackPressed()

        val button = view.findViewById<Button>(R.id.yesBtn)
        button.setOnClickListener(bluetoothBtnListener)
        return view
    }

    private val bluetoothBtnListener = View.OnClickListener {
        val bluetoothPageFrag = BluetoothPageFragment()
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.nav_host_fragment, bluetoothPageFrag)
            .commit()
    }
}