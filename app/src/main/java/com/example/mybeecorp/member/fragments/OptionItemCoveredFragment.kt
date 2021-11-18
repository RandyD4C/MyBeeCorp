package com.example.mybeecorp.member.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mybeecorp.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OptionItemCoveredFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OptionItemCoveredFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var itemCoveredName: String? = null
    private var itemCoveredPrice: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemCoveredName = it.getString(ARG_PARAM1)
            itemCoveredPrice = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_option_item_covered, container, false)
        view.findViewById<TextView>(R.id.itemCoveredName_TextView).text = itemCoveredName
        view.findViewById<TextView>(R.id.itemCoveredPrice_TextView).text = itemCoveredPrice

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param itemCoveredName  Parameter 1.
         * @param itemCoveredPrice Parameter 2.
         * @return A new instance of fragment OptionItemCoveredFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(itemCoveredName : String, itemCoveredPrice: String) =
            OptionItemCoveredFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, itemCoveredName)
                    putString(ARG_PARAM2, itemCoveredPrice)
                }
            }
    }
}