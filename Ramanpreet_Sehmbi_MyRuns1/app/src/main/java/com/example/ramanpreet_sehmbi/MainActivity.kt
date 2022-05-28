package com.example.ramanpreet_sehmbi

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private var WRITEPERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    private var CAMERAPERMISSION = android.Manifest.permission.CAMERA
    private var REQUESTCODE = 100
    private var SHAREDPREFERENCE = "USERPROFILE"

    lateinit var capturedImageUri: Uri
    private lateinit var cameraCapturedPicture: ActivityResultLauncher<Intent>
    private lateinit var usrProfileImageViewModal: UserProfileViewModal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadProfile()
        setAutocompleteMajor()
        cleanUp()

        // Validators
        onPasswordChangeListener()
        onEmailChangeListener()
        onNameChangeListener()
        onClassChangeListener()
        onMajorChangeListener()
        setupCameraStorageHandling()
    }

    fun cleanUp(){
        val junkFiles = ArrayList<String>()
        val directory = getExternalFilesDir(null).toString() + "/"
        File(directory).walkTopDown().forEach {
            if (it.path.toString().endsWith(".jpg")){
                junkFiles.add(it.path.toString())
            }
        }
        val sharedPreference =  getSharedPreferences(SHAREDPREFERENCE,Context.MODE_PRIVATE)
        if (sharedPreference.contains("userName")){
            if (junkFiles.contains(sharedPreference.getString("userImagePath","").toString())){
                println("Looking for $junkFiles")
                junkFiles.remove(sharedPreference.getString("userImagePath","").toString())
            }

        }
       for (file in junkFiles){
            Files.deleteIfExists(Paths.get(file))
       }
    }
    private fun loadProfile() {
        val sharedPreference =  getSharedPreferences(SHAREDPREFERENCE,Context.MODE_PRIVATE)
        val nameEditText = findViewById<EditText>(R.id.user_name_id)
        val emailEditText = findViewById<EditText>(R.id.email_address_id)
        val userGenderRadioGroup = findViewById<RadioGroup>(R.id.gender_group_id)
        val phoneEditText = findViewById<EditText>(R.id.phone_number_id)
        val userClassYear = findViewById<EditText>(R.id.class_id)
        val userMajor = findViewById<EditText>(R.id.profile_major_id)

        if (sharedPreference.contains("userName")){
            nameEditText.setText(sharedPreference.getString("userName",""))
            emailEditText.setText(sharedPreference.getString("userEmail",""))
            userGenderRadioGroup.check((sharedPreference.getString("userGender","")!!.toInt()))
            phoneEditText.setText(sharedPreference.getString("userPhone",""))
            userClassYear.setText(sharedPreference.getString("userClass",""))
            userMajor.setText(sharedPreference.getString("userMajor",""))
            var imageFilePath = sharedPreference.getString("userImagePath","").toString()
            var imageFile = File(imageFilePath)
            if(imageFile.exists()){
                setImageFromFilePath(imageFilePath)
            }
        }
        else{
            return
        }
    }

    fun setAutocompleteMajor(){
        val majorAutoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.profile_major_id)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, majorListNames)
        majorAutoCompleteTextView.setAdapter(adapter)
        majorAutoCompleteTextView.threshold = 1
        majorAutoCompleteTextView.setAdapter(adapter)
    }

    fun onNameChangeListener(){
        val nameEditText = findViewById<EditText>(R.id.user_name_id)
        nameEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    nameEditText.setError("User Name is required")
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                }
        })
    }

    fun onClassChangeListener(){
        val classEditText = findViewById<EditText>(R.id.class_id)
        classEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    classEditText.setError("Class Year is required")
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })
    }

    fun onMajorChangeListener(){
        val userMajorEditText = findViewById<EditText>(R.id.profile_major_id)
        userMajorEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    userMajorEditText.setError("Major is required")
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })
    }

    fun onEmailChangeListener(){
        val emailEditText = findViewById<EditText>(R.id.email_address_id)
        emailEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!validateEmail(s)){
                    emailEditText.setError("Invalid Email Address")
                }
            }
        })
    }

    fun onPasswordChangeListener(){
        val phoneEditText = findViewById<EditText>(R.id.phone_number_id)
        phoneEditText.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        phoneEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!validatePhonenumber(s)){
                    phoneEditText.setError("Invalid Phone Number")
                }
            }
        })

    }

    fun changeUserImage(view: View) {
        if (ContextCompat.checkSelfPermission(this, CAMERAPERMISSION) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this, WRITEPERMISSION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERAPERMISSION, WRITEPERMISSION),REQUESTCODE);
        } else {
            handleImageCaptureandStorage()
        }
    }

    fun setImageFromFilePath(path: String){
        val imgBitmap = BitmapFactory.decodeFile(path)
        val matrix = Matrix()
        matrix.postRotate(90f)
        val rotatedFile = Bitmap.createBitmap(
            imgBitmap,
            0,
            0,
            imgBitmap.getWidth(),
            imgBitmap.getHeight(),
            matrix,
            true
        )
        val usrProfileImage = findViewById<ImageView>(R.id.user_profile_image_id)
        usrProfileImage.setImageBitmap(rotatedFile)
    }

    fun getProfilePictureName(): String {
        val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val currentDataTime: String = sdf.format(Date())
        return "$currentDataTime.jpg"
    }
    fun getRotatedBitmapPicture(imgFile: File): Bitmap? {
        val imgBitmap = BitmapFactory.decodeFile(imgFile.path)
        val matrix = Matrix()
        matrix.postRotate(90f)
        return Bitmap.createBitmap(
            imgBitmap,
            0,
            0,
            imgBitmap.getWidth(),
            imgBitmap.getHeight(),
            matrix,
            true
        )
    }
    fun setupCameraStorageHandling(){
        val profilePictureName = getProfilePictureName()
        var imgFile = File(getExternalFilesDir(null), profilePictureName)
        capturedImageUri = FileProvider.getUriForFile(this, "com.example.ramanpreet_sehmbi", imgFile)

        usrProfileImageViewModal = ViewModelProvider(this).get(UserProfileViewModal::class.java)
        usrProfileImageViewModal.userProfileImage.observe(this) {
            val usrProfileImage = findViewById<ImageView>(R.id.user_profile_image_id)
            usrProfileImage.setImageBitmap(it)
        }

        cameraCapturedPicture = registerForActivityResult(StartActivityForResult()){ result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                usrProfileImageViewModal.userProfileImage.value = getRotatedBitmapPicture(imgFile)
            }
        }
    }

    fun handleImageCaptureandStorage(){
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri)
        cameraCapturedPicture.launch(cameraIntent)
    }

    fun openSettings(){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + this.packageName)
        startActivity(intent)
    }

    fun showAlertDialog(){
        var openSettingbuilder = androidx.appcompat.app.AlertDialog.Builder(this)
        openSettingbuilder.setTitle(getString(R.string.Permissions))
        openSettingbuilder.setMessage(getString(R.string.PermissionDialogBody))
        openSettingbuilder.setPositiveButton(getString(R.string.Yes), DialogInterface.OnClickListener{ dialog, id ->
            openSettings()
            dialog.cancel()
        })
        openSettingbuilder.setNegativeButton(getString(R.string.No), DialogInterface.OnClickListener{ dialog, id ->
            dialog.cancel()
        })
        openSettingbuilder.create().show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                handleImageCaptureandStorage()
            } else {
                showAlertDialog()
            }
        }

    }

    fun validateEditTextBoxes(): Boolean {
        val nameEditText = findViewById<EditText>(R.id.user_name_id)
        val emailEditText = findViewById<EditText>(R.id.email_address_id)
        val phoneEditText = findViewById<EditText>(R.id.phone_number_id)
        val userClassYear = findViewById<EditText>(R.id.class_id)
        val userMajor = findViewById<EditText>(R.id.profile_major_id)

        return !nameEditText.text.isNullOrEmpty() and
                !emailEditText.text.isNullOrEmpty() and
                !phoneEditText.text.isNullOrEmpty() and
                !userClassYear.text.isNullOrEmpty() and
                !userMajor.text.isNullOrEmpty() and
                validatePhonenumber(phoneEditText.text.toString()) and
                validateEmail(emailEditText.text.toString())
    }

    fun checkifUpdate(path: String): Boolean{
        val imageFilePath = path
        val imageFile = File(imageFilePath)
        if(imageFile.exists()){
            return true
        }
        return false
    }

    fun updateOldPath(sharedPreference:  SharedPreferences.Editor){
        val sharedOldPreference =  getSharedPreferences(SHAREDPREFERENCE,Context.MODE_PRIVATE)
        if (sharedOldPreference.contains("userName")) {
            val imageFilePath = sharedOldPreference.getString("userImagePath","").toString()
            if (imageFilePath != "")
            {
                sharedPreference.putString("userImagePath", imageFilePath)
            }
        }
    }

    fun saveProfile(view: View) {
        if (validateEditTextBoxes()){
            val sharedPreference =  getSharedPreferences(SHAREDPREFERENCE,Context.MODE_PRIVATE)

            val nameEditText = findViewById<EditText>(R.id.user_name_id)
            val emailEditText = findViewById<EditText>(R.id.email_address_id)
            val userGenderRadioGroup = findViewById<RadioGroup>(R.id.gender_group_id)
            val phoneEditText = findViewById<EditText>(R.id.phone_number_id)
            val userClassYear = findViewById<EditText>(R.id.class_id)
            val userMajor = findViewById<EditText>(R.id.profile_major_id)

            val sharedPrefEditor = sharedPreference.edit()
            sharedPrefEditor.putString(getString(R.string.sharedPUserName), nameEditText.text.toString())
            sharedPrefEditor.putString(getString(R.string.sharePUserEmail), emailEditText.text.toString())
            sharedPrefEditor.putString(getString(R.string.sharedPUserPhone),  phoneEditText.text.toString())
            sharedPrefEditor.putString(getString(R.string.sharedPUserGender), userGenderRadioGroup.checkedRadioButtonId.toString())
            sharedPrefEditor.putString(getString(R.string.sharedPUserClass), userClassYear.text.toString())
            sharedPrefEditor.putString(getString(R.string.sharedPUserMajor), userMajor.text.toString())
            if (checkifUpdate(capturedImageUri.path.toString())){
                sharedPrefEditor.putString("userImagePath", capturedImageUri.path)
            }
            else {
                updateOldPath(sharedPrefEditor)
            }
            sharedPrefEditor.apply()
            Toast.makeText(this, "User Profile Saved", Toast.LENGTH_SHORT).show()
            this.finish()
        }
        else
        {
            Toast.makeText(this, "Please validate all the fields", Toast.LENGTH_SHORT).show()
        }

    }

    fun closeApplication(view: View) {
        this.finish()
    }

}