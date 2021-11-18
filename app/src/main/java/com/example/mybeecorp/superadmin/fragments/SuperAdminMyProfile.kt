package com.example.mybeecorp.superadmin.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import com.bumptech.glide.Glide
import com.example.mybeecorp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class SuperAdminMyProfile : AppCompatActivity() {

    var selectedPhoto: Uri? = null
    var changedPhoto: String? = "No"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_super_admin_my_profile)
        this.supportActionBar?.title = "My Profile"
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val userID = FirebaseAuth.getInstance().uid
        //val userID = "usr10002"

        val editProfilePic = findViewById<ImageView>(R.id.superAdminUserProfile_editPicture)
        val saveBtn = findViewById<Button>(R.id.superAdminUserProfile_Save)

        loadProfile(userID!!)

        editProfilePic.setOnClickListener{
            val intentPickPhoto = Intent(Intent.ACTION_PICK)
            intentPickPhoto.type = "image/*"
            startActivityForResult(intentPickPhoto, 0)
        }

        saveBtn.setOnClickListener{
            if(changedPhoto == "Yes") {
                uploadImageDatabase(userID)
            }else{
                val oldPic = findViewById<TextView>(R.id.oldPicUrl)
                updateProfile(userID, oldPic.text.toString())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val circleView = findViewById<CircleImageView>(R.id.superAdminUserProfile_outputPicture)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhoto = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhoto)
            circleView.setImageBitmap(bitmap)
            changedPhoto = "Yes"
        }
    }

    private fun uploadImageDatabase(userUID:String){
        if(selectedPhoto == null) return
        val filename = UUID.randomUUID().toString()
        val photoRef = FirebaseStorage.getInstance().getReference("/images/$filename")

        photoRef.putFile(selectedPhoto!!)
            .addOnSuccessListener {
                photoRef.downloadUrl.addOnSuccessListener {
                    updateProfile(userUID, it.toString())
                }
            }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    private fun loadProfile(userID:String){
        var userNameET = findViewById<EditText>(R.id.superAdminUserProfile_userName)
        var phoneNumET = findViewById<EditText>(R.id.superAdminUserProfile_PhoneNum)
        var emailET = findViewById<EditText>(R.id.superAdminUserProfile_email)
        var userProfileCIV = findViewById<CircleImageView>(R.id.superAdminUserProfile_outputPicture)
        var pointsET = findViewById<EditText>(R.id.superAdminUserProfile_pointsAwarded)
        var spinsET = findViewById<EditText>(R.id.superAdminUserProfile_spins)
        var userRoleET = findViewById<EditText>(R.id.superAdminUserProfile_userRole)
        var bankNumET = findViewById<EditText>(R.id.superAdminUserProfile_bankNum)
        var userStatusSpinner = findViewById<Spinner>(R.id.superAdminUserProfile_userStatus)
        val origBankPINET = findViewById<TextView>(R.id.hiddenOrigBankPIN)
        val oldPicUrl = findViewById<TextView>(R.id.oldPicUrl)
        var bankpinOriET = findViewById<EditText>(R.id.superAdminUserProfile_origPIN)
        val userStatus = resources.getStringArray(R.array.userStatus)
        var origBankNumET = findViewById<TextView>(R.id.superAdmin_hiddenBankNum)

        if(userStatusSpinner != null){
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, userStatus
            )
            userStatusSpinner.adapter = adapter
        }

        /*var profileRef = Firebase.database("https://yangloomadtestdatabase-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("User")*/
        var profileRef = Firebase.database("https://mybeecorp-cdb46-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("User")

        profileRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bankpinOriET.isEnabled = true
                if(snapshot.exists()){
                    for(i in snapshot.children){
                        if(i.child("user_uid").value.toString() == userID){
                            if(i.child("user_avatar").value.toString() != "") {
                                if(!this@SuperAdminMyProfile.isFinishing) {
                                    Glide.with(userProfileCIV.context)
                                        .load(i.child("user_avatar").value)
                                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                                        .circleCrop().error(
                                            R.drawable.ic_baseline_account_circle_24
                                        ).into(userProfileCIV)
                                }
                            }
                            userNameET.setText(i.child("user_full_name").value.toString())
                            phoneNumET.setText(i.child("user_phone_num").value.toString())
                            emailET.setText(i.child("user_email").value.toString())
                            pointsET.setText(i.child("point_awarded").value.toString())
                            spinsET.setText(i.child("spin_awarded").value.toString())
                            userRoleET.setText(i.child("user_role").value.toString())
                            bankNumET.setText(i.child("user_bank_card_num").value.toString())
                            origBankNumET.text = i.child("user_bank_card_num").value.toString()
                            if(i.child("user_bank_card_num").value.toString() == ""){
                                bankpinOriET.isEnabled = false
                            }
                            if(i.child("user_status").value != "Activate"){
                                userStatusSpinner.setSelection(1)
                            }
                            origBankPINET.text = i.child("user_bank_pin").value.toString()
                            oldPicUrl.text = i.child("user_avatar").value.toString()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "An error has occurred.", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun updateProfile(userID:String, photoUrl:String){
        var userNameET = findViewById<EditText>(R.id.superAdminUserProfile_userName)
        var phoneNumET = findViewById<EditText>(R.id.superAdminUserProfile_PhoneNum)
        var bankNumET = findViewById<EditText>(R.id.superAdminUserProfile_bankNum)
        val userStatusSpinner = findViewById<Spinner>(R.id.superAdminUserProfile_userStatus)
        var bankpinOriET = findViewById<EditText>(R.id.superAdminUserProfile_origPIN)
        var bankpinNewET = findViewById<EditText>(R.id.superAdminUserProfile_newPIN)
        val hiddenBankPin = findViewById<TextView>(R.id.hiddenOrigBankPIN)
        var updateBankPIN = hiddenBankPin.text.toString()
        val hiddenBankNum = findViewById<TextView>(R.id.superAdmin_hiddenBankNum)

        //val profileRef = Firebase.database("https://yangloomadtestdatabase-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("User")
        val profileRef = Firebase.database("https://mybeecorp-cdb46-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("User")

        val validPhoneNum = isValidPhoneNum(phoneNumET.text.toString())

        if(userNameET.text.toString().trim().isNotEmpty()){
            if(phoneNumET.text.toString().trim().isNotEmpty() && validPhoneNum){
                if(bankNumET.text.toString().trim().isEmpty()){
                    if(bankpinNewET.text.toString().trim().isEmpty()){
                        val updateSuperAdmin = mapOf<String, String>(
                            "user_full_name" to userNameET.text.toString(),
                            "user_phone_num" to phoneNumET.text.toString(),
                            "user_status" to userStatusSpinner.selectedItem.toString(),
                            "user_avatar" to photoUrl
                        )

                        profileRef.child(userID).updateChildren(updateSuperAdmin)
                            .addOnCompleteListener {
                                Toast.makeText(
                                    applicationContext,
                                    "Profile updated successfully",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                    }else{
                        Toast.makeText(
                            applicationContext,
                            "Please insert Bank Number",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        bankNumET.requestFocus()
                        return
                    }
                }else if(bankNumET.text.toString().trim().isNotEmpty() && bankNumET.text.toString().length == 16){
                    if(bankpinNewET.text.toString().trim().isNotEmpty()){
                        if(bankpinOriET.text.toString() == updateBankPIN) {
                            if (bankpinNewET.text.toString().length == 6) {
                                updateBankPIN = bankpinNewET.text.toString()

                                val updateSuperAdmin = mapOf<String, String>(
                                    "user_full_name" to userNameET.text.toString(),
                                    "user_phone_num" to phoneNumET.text.toString(),
                                    "user_bank_card_num" to bankNumET.text.toString(),
                                    "user_status" to userStatusSpinner.selectedItem.toString(),
                                    "user_bank_pin" to updateBankPIN,
                                    "user_avatar" to photoUrl
                                )

                                profileRef.child(userID).updateChildren(updateSuperAdmin)
                                    .addOnCompleteListener {
                                        Toast.makeText(
                                            applicationContext,
                                            "Profile updated successfully",
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                    }
                            }else{
                                Toast.makeText(
                                    applicationContext,
                                    "Bank PIN must be 6 digits",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                bankpinNewET.requestFocus()
                                return
                            }
                        }else if(updateBankPIN == ""){
                            if (bankpinNewET.text.toString().length == 6) {
                                updateBankPIN = bankpinNewET.text.toString()

                                val updateSuperAdmin = mapOf<String, String>(
                                    "user_full_name" to userNameET.text.toString(),
                                    "user_phone_num" to phoneNumET.text.toString(),
                                    "user_bank_card_num" to bankNumET.text.toString(),
                                    "user_status" to userStatusSpinner.selectedItem.toString(),
                                    "user_bank_pin" to updateBankPIN,
                                    "user_avatar" to photoUrl
                                )

                                profileRef.child(userID).updateChildren(updateSuperAdmin)
                                    .addOnCompleteListener {
                                        Toast.makeText(
                                            applicationContext,
                                            "Profile updated successfully",
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                    }
                            }else{
                                Toast.makeText(
                                    applicationContext,
                                    "Bank PIN must be 6 digits",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                bankpinNewET.requestFocus()
                                return
                            }
                        }
                        else {
                            Toast.makeText(
                                applicationContext,
                                "Original PIN is wrong",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            bankpinOriET.requestFocus()
                            return
                        }
                    }else{
                        if(hiddenBankNum.text.toString() == ""){
                            Toast.makeText(
                                applicationContext,
                                "Please insert bank PIN",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            bankpinNewET.requestFocus()
                            return
                        }else{
                            val updateSuperAdmin = mapOf<String,String>(
                                "user_full_name" to userNameET.text.toString(),
                                "user_phone_num" to phoneNumET.text.toString(),
                                "user_bank_card_num" to bankNumET.text.toString(),
                                "user_status" to userStatusSpinner.selectedItem.toString(),
                                "user_avatar" to photoUrl
                            )

                            profileRef.child(userID).updateChildren(updateSuperAdmin).addOnCompleteListener {
                                Toast.makeText(applicationContext, "Profile updated successfully", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
                }
                else{
                    Toast.makeText(
                        applicationContext,
                        "Bank Number must be 16 digits",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    bankNumET.requestFocus()
                    return
                }
            }
            else{
                Toast.makeText(
                    applicationContext,
                    "Invalid Phone Number",
                    Toast.LENGTH_LONG
                )
                    .show()
                phoneNumET.requestFocus()
                return
            }
        }
        else{
            Toast.makeText(
                applicationContext,
                "Full name cannot be empty",
                Toast.LENGTH_LONG
            )
                .show()
            userNameET.requestFocus()
            return
        }
    }

    private fun isValidPhoneNum(phone: String?): Boolean {
        phone?.let {
            val phonePattern = "^(\\+?6?01)[0-46-9]-*[0-9]{7,8}\$"
            val phoneMatcher = Regex(phonePattern)

            return phoneMatcher.find(phone) != null
        } ?: return false
    }
}