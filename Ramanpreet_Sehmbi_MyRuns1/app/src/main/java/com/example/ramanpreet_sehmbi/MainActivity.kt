package com.example.ramanpreet_sehmbi

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager
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
import com.example.ramanpreet_sehmbi.CameraGalleryDialog.Companion.CAMERA
import com.example.ramanpreet_sehmbi.CameraGalleryDialog.Companion.GALLERY
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private var WRITEPERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    private var CAMERAPERMISSION = android.Manifest.permission.CAMERA
    private var REQUESTCODE = 100
    private var SHAREDPREFERENCE = "USERPROFILE"
    private var IMAGEURI: Uri? = null

    lateinit var capturedImageUri: Uri
    private lateinit var cameraCapturedPicture: ActivityResultLauncher<Intent>
    private lateinit var galleryCapturedPicture: ActivityResultLauncher<Intent>

    private val TMP_IMG_URI = "IMG_URI"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadProfile(savedInstanceState)
        setAutocompleteMajor()

        // Validators
        onPasswordChangeListener()
        onEmailChangeListener()
        onNameChangeListener()
        onClassChangeListener()
        onMajorChangeListener()
        setupCameraStorageHandling()
    }

    private fun loadProfile(savedInstanceState: Bundle?) {
        val sharedPreference = getSharedPreferences(SHAREDPREFERENCE, Context.MODE_PRIVATE)
        val nameEditText = findViewById<EditText>(R.id.user_name_id)
        val emailEditText = findViewById<EditText>(R.id.email_address_id)
        val userGenderRadioGroup = findViewById<RadioGroup>(R.id.gender_group_id)
        val phoneEditText = findViewById<EditText>(R.id.phone_number_id)
        val userClassYear = findViewById<EditText>(R.id.class_id)
        val userMajor = findViewById<EditText>(R.id.profile_major_id)

        if (savedInstanceState != null) {
            val imageURIString = savedInstanceState.getString(TMP_IMG_URI)
            if (imageURIString != null) {
                val imageUrl = Uri.parse("$imageURIString");
                val usrProfileImage = findViewById<ImageView>(R.id.user_profile_image_id)
                IMAGEURI = imageUrl
                usrProfileImage.setImageURI(imageUrl)
            }
        } else {
            if (sharedPreference.contains("userImagePath")) {
                val imageURIString = sharedPreference.getString("userImagePath", "").toString()
                if (imageURIString != "") {
                    val imageUrl = Uri.parse(imageURIString);
                    val usrProfileImage = findViewById<ImageView>(R.id.user_profile_image_id)
                    IMAGEURI = imageUrl
                    usrProfileImage.setImageURI(imageUrl)
                }
            }
        }
        if (sharedPreference.contains("userName")) {
            nameEditText.setText(sharedPreference.getString("userName", ""))
            emailEditText.setText(sharedPreference.getString("userEmail", ""))
            userGenderRadioGroup.check((sharedPreference.getString("userGender", "")!!.toInt()))
            phoneEditText.setText(sharedPreference.getString("userPhone", ""))
            userClassYear.setText(sharedPreference.getString("userClass", ""))
            userMajor.setText(sharedPreference.getString("userMajor", ""))
        } else {
            return
        }
    }

    fun setAutocompleteMajor() {
        val majorAutoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.profile_major_id)
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, majorListNames)
        majorAutoCompleteTextView.setAdapter(adapter)
        majorAutoCompleteTextView.threshold = 1
        majorAutoCompleteTextView.setAdapter(adapter)
    }

    fun onNameChangeListener() {
        val nameEditText = findViewById<EditText>(R.id.user_name_id)
        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    nameEditText.setError("User Name is required")
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })
    }

    fun onClassChangeListener() {
        val classEditText = findViewById<EditText>(R.id.class_id)
        classEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    classEditText.setError("Class Year is required")
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })
    }

    fun onMajorChangeListener() {
        val userMajorEditText = findViewById<EditText>(R.id.profile_major_id)
        userMajorEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    userMajorEditText.setError("Major is required")
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })
    }

    fun onEmailChangeListener() {
        val emailEditText = findViewById<EditText>(R.id.email_address_id)
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!validateEmail(s)) {
                    emailEditText.setError("Invalid Email Address")
                }
            }
        })
    }

    fun onPasswordChangeListener() {
        val phoneEditText = findViewById<EditText>(R.id.phone_number_id)
        phoneEditText.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        phoneEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!validatePhonenumber(s)) {
                    phoneEditText.setError("Invalid Phone Number")
                }
            }
        })

    }

    fun changeUserImage(view: View) {
        if (ContextCompat.checkSelfPermission(
                this,
                CAMERAPERMISSION
            ) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(
                this,
                WRITEPERMISSION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(CAMERAPERMISSION, WRITEPERMISSION),
                REQUESTCODE
            );
        } else {
            val profilePicturePicker = CameraGalleryDialog()
            profilePicturePicker.show(supportFragmentManager, null)
            supportFragmentManager.setFragmentResultListener(
                "OPTION_REQUEST_KEY",
                this
            ) { resultkey, bundle ->
                if (resultkey == "OPTION_REQUEST_KEY") {
                    val option = bundle.get("OPTION_SELECTED")
                    if (option == GALLERY) {
                        handleGalleryImageStorage()
                    } else if (option == CAMERA) {
                        handleImageCaptureandStorage()
                    }
                }
            }
        }
    }


    fun getProfilePictureName(): String {
        val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val currentDataTime: String = sdf.format(Date())
        return "$currentDataTime.jpg"
    }

    fun setupCameraStorageHandling() {
        val profilePictureName = getProfilePictureName()
        var imgFile = File(getExternalFilesDir(null), profilePictureName)
        capturedImageUri =
            FileProvider.getUriForFile(this, "com.example.ramanpreet_sehmbi", imgFile)
        cameraCapturedPicture =
            registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val usrProfileImage = findViewById<ImageView>(R.id.user_profile_image_id)
                    usrProfileImage.setImageURI(capturedImageUri)
                    if (imgFile.exists()) {
                        IMAGEURI = capturedImageUri
                    }
                }
            }

        galleryCapturedPicture =
            registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val selectedImageUri: Uri? = result.data?.data
                    val usrProfileImage = findViewById<ImageView>(R.id.user_profile_image_id)
                    usrProfileImage.setImageURI(selectedImageUri)
                    if (selectedImageUri != null) {
                        IMAGEURI = selectedImageUri
                    }
                }
            }
    }

    fun handleImageCaptureandStorage() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri)
        cameraCapturedPicture.launch(cameraIntent)
    }

    fun handleGalleryImageStorage() {
        val galleryIntent = Intent(ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri)
        galleryCapturedPicture.launch(galleryIntent)
    }

    fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + this.packageName)
        startActivity(intent)
    }

    fun showAlertDialog() {
        var openSettingbuilder = androidx.appcompat.app.AlertDialog.Builder(this)
        openSettingbuilder.setTitle(getString(R.string.Permissions))
        openSettingbuilder.setMessage(getString(R.string.PermissionDialogBody))
        openSettingbuilder.setPositiveButton(
            getString(R.string.Yes),
            DialogInterface.OnClickListener { dialog, id ->
                openSettings()
                dialog.cancel()
            })
        openSettingbuilder.setNegativeButton(
            getString(R.string.No),
            DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })
        openSettingbuilder.create().show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
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

    fun saveProfile(view: View) {
        if (validateEditTextBoxes()) {
            val sharedPreference = getSharedPreferences(SHAREDPREFERENCE, Context.MODE_PRIVATE)

            val nameEditText = findViewById<EditText>(R.id.user_name_id)
            val emailEditText = findViewById<EditText>(R.id.email_address_id)
            val userGenderRadioGroup = findViewById<RadioGroup>(R.id.gender_group_id)
            val phoneEditText = findViewById<EditText>(R.id.phone_number_id)
            val userClassYear = findViewById<EditText>(R.id.class_id)
            val userMajor = findViewById<EditText>(R.id.profile_major_id)

            val sharedPrefEditor = sharedPreference.edit()
            sharedPrefEditor.putString(
                getString(R.string.sharedPUserName),
                nameEditText.text.toString()
            )
            sharedPrefEditor.putString(
                getString(R.string.sharePUserEmail),
                emailEditText.text.toString()
            )
            sharedPrefEditor.putString(
                getString(R.string.sharedPUserPhone),
                phoneEditText.text.toString()
            )
            sharedPrefEditor.putString(
                getString(R.string.sharedPUserGender),
                userGenderRadioGroup.checkedRadioButtonId.toString()
            )
            sharedPrefEditor.putString(
                getString(R.string.sharedPUserClass),
                userClassYear.text.toString()
            )
            sharedPrefEditor.putString(
                getString(R.string.sharedPUserMajor),
                userMajor.text.toString()
            )

            if (IMAGEURI.toString() != "") {
                sharedPrefEditor.putString("userImagePath", IMAGEURI.toString())
            }
            sharedPrefEditor.apply()
            Toast.makeText(this, "User Profile Saved", Toast.LENGTH_SHORT).show()
            this.finish()
        } else {
            Toast.makeText(this, "Please validate all the fields", Toast.LENGTH_SHORT).show()
        }

    }

    fun closeApplication(view: View) {
        this.finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (IMAGEURI != null) {
            outState.putString(TMP_IMG_URI, IMAGEURI.toString())
        }
    }
}
