package com.example.mybeecorp.navigation.superadmin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.mybeecorp.R
import com.example.mybeecorp.superadmin.activities.InsCompanySuperAdminMainPage
import com.google.android.material.card.MaterialCardView


class CompanyFragment_SuperAdmin : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_company_super_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val buttonCompany = view.findViewById<MaterialCardView>(R.id.button_company)

        buttonCompany.setOnClickListener {
            val intent = Intent(view.context, InsCompanySuperAdminMainPage::class.java)
            view.context.startActivity(intent)
        }
    }
}