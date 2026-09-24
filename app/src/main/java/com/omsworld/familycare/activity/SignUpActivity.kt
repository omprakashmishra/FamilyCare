package com.omsworld.familycare.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import com.omsworld.familycare.R
import com.omsworld.familycare.adapter.CountryCodeAdapter
import com.omsworld.familycare.api_call.AppUtil
import com.omsworld.familycare.api_call.CallWebService
import com.omsworld.familycare.api_call.CommonFunctions
import com.omsworld.familycare.api_call.FieldUtils
import com.omsworld.familycare.api_call.GlobalConstants
import com.omsworld.familycare.api_call.MyServiceListener
import com.omsworld.familycare.model.CountryCodeModel
import com.omsworld.familycare.setting.BaseActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SignUpActivity : BaseActivity() {

    companion object {
        private const val TAG = "SignUpActivity"
    }

    private lateinit var binding: ActivitySignUpBinding

    private var valid = false

    private var username = ""
    private var email = ""
    private var countyCd = ""
    private var mobile = ""
    private var password = ""
    private var confirmPwd = ""
    private var mobileNumber = ""
    private var invitationCode = ""

    private val spinnerArray2 = ArrayList<CountryCodeModel>()

    private var countryCodeAdapter: CountryCodeAdapter? = null
    private var countryCodeModel: CountryCodeModel? = null

    private lateinit var commonFunctions: CommonFunctions

    private var inviteeStatusId = "1080"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        commonFunctions = CommonFunctions(this)

        binding.edtUsername.requestFocus()

        setupCountryCodeSpinner()
        setupClickListeners()

        // Uncomment when country-code API needs to be called.
        // setCountryCodeAPI()
    }

    private fun setupCountryCodeSpinner() {

        binding.spCountrycode.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    countyCd = if (position == 0) {
                        ""
                    } else {
                        spinnerArray2[position].CountryCode.toString()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Nothing to do
                }
            }
    }

    private fun setupClickListeners() {

        /*
         * Emergency Contact checkbox
         */
        binding.ckInvitee.setOnClickListener {
            inviteeStatusId = "1079"
            forNewUserWithCheckBox()
        }

        /*
         * Subscription checkbox
         */
        binding.subscriptionCheckbox.setOnClickListener { view ->

            commonFunctions.hideKeyboard(view)

            if (binding.subscriptionCheckbox.isChecked) {

                binding.btnSubscribe.isEnabled = true

                binding.btnSubscribe.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.theme_color
                    )
                )

            } else {

                binding.btnSubscribe.isEnabled = false

                binding.btnSubscribe.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.RedFadeColor
                    )
                )
            }
        }

        /*
         * Terms and Conditions
         */
        binding.tvTermsconditions.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    TermAndConditionsAc::class.java
                )
            )
        }

        /*
         * Register
         */
        binding.btnSubscribe.setOnClickListener {
            registrationAPI()
        }
    }

    private fun setCountryCodeAPI() {

        countryCodeModel = CountryCodeModel().apply {
            CountryCode = "0"
            CountryName = "Country Code"
        }

        spinnerArray2.add(countryCodeModel!!)

        myservice = CallWebService(
            "",
            this,
            urlList.CountryCode,
            commonFunctions.GetOrganization(""),
            object : MyServiceListener {

                override fun onSuccess(string: String) {

                    Log.d(
                        "----Country data for SignUp->",
                        string
                    )

                    try {

                        val jsonObject = JSONObject(string)

                        val status = jsonObject.optInt("Status")

                        if (status == 1) {

                            val dataArray: JSONArray =
                                jsonObject.optJSONArray("Data")
                                    ?: return

                            for (i in 0 until dataArray.length()) {

                                val dataObject =
                                    dataArray.getJSONObject(i)

                                val countryCode =
                                    dataObject.optString("CountryCode")

                                val countryName =
                                    dataObject.optString("CountryName")

                                countryCodeModel =
                                    CountryCodeModel().apply {
                                        CountryCode = countryCode
                                        CountryName = countryName
                                    }

                                spinnerArray2.add(
                                    countryCodeModel!!
                                )
                            }

                            countryCodeAdapter =
                                CountryCodeAdapter(
                                    this@SignUpActivity,
                                    R.layout.item_collages,
                                    spinnerArray2
                                )

                            binding.spCountrycode.adapter =
                                countryCodeAdapter
                        }

                    } catch (e: JSONException) {

                        Log.e(
                            TAG,
                            "Country code JSON error",
                            e
                        )

                    } catch (e: Exception) {

                        Log.e(
                            TAG,
                            "Country code error",
                            e
                        )
                    }
                }

                override fun onFailed() {
                    Log.e(TAG, "Country code API failed")
                }
            }
        )
    }

    private fun validate() {

        valid = true

        if (!binding.subscriptionCheckbox.isChecked) {

            valid = false

        } else if (
            FieldUtils.isBlank(
                binding.edtUsername.text.toString()
            )
        ) {

            binding.edtUsername.error =
                "Please enter a username."

            valid = false

        } else if (
            FieldUtils.isBlank(
                binding.edtEmail.text.toString()
            )
        ) {

            binding.edtEmail.error =
                "Please enter a valid email address."

            valid = false

        } else if (
            email.isNotEmpty() &&
            !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        ) {

            binding.edtEmail.error =
                "Please enter a valid email address."

            valid = false

        } else if (
            FieldUtils.isBlank(mobile) ||
            mobile.length > 11 ||
            mobile.length < 10
        ) {

            binding.edtMobile.error =
                "Please enter a valid mobile number."

            valid = false

        } else if (
            FieldUtils.isBlank(password)
        ) {

            binding.edtPassword.error =
                "Please enter a password."

            valid = false

        } else if (password.length < 2) {

            binding.edtPassword.error =
                "Enter minimum 2-digit password"

            valid = false

        } else if (
            FieldUtils.isBlank(confirmPwd)
        ) {

            binding.edtConfirmPassword.error =
                "Please reconfirm the entered password."

            valid = false

        } else if (password != confirmPwd) {

            binding.edtConfirmPassword.error =
                "Passwords entered do not match. Please try again."

            valid = false
        }
    }

    private fun forNewUserWithCheckBox() {

        binding.llInvitationCode.visibility =
            View.VISIBLE

        if (!binding.ckInvitee.isChecked) {

            inviteeStatusId = "1080"

            binding.llInvitationCode.visibility =
                View.GONE
        }
    }

    private fun registrationAPI() {

        if (!isInternetAvailable()) {

            tstSnake(
                binding.root,
                "Internet connection failed !"
            )

            return
        }

        username = binding.edtUsername.text
            .toString()
            .trim()

        email = binding.edtEmail.text
            .toString()
            .trim()

        mobile = binding.edtMobile.text
            .toString()
            .trim()

        mobileNumber = mobile

        password = binding.edtPassword.text
            .toString()

        confirmPwd = binding.edtConfirmPassword.text
            .toString()

        invitationCode = binding.eTOne.text
            .toString()
            .trim()

        valid = true

        validate()

        if (!valid) {
            return
        }

        val deviceId = deviceId()

        val deviceTokenId =
            commonFunctions.myPreference.getString(
                this,
                GlobalConstants.Firebasetoken
            )

        Log.d(TAG, "Device ID: $deviceId")
        Log.d(TAG, "Device Token: $deviceTokenId")

        myservice = CallWebService(
            this@SignUpActivity,
            urlList.reg_with_mob,
            commonFunctions.reg_with_mob(
                username,
                email,
                mobileNumber,
                password
            ),
            this@SignUpActivity
        )
    }

    override fun onSuccess(string: String) {

        super.onSuccess(string)

        log_(
            "---------SignUpAc--API---Registration>",
            string
        )

        responseListener(string)
    }

    private fun responseListener(string: String) {

        try {

            val flag = JSONObject(string)

            val status = flag.getString("status")
            val message = flag.getString("message")

            if (status == "0") {

                tstSnake(
                    binding.root,
                    message
                )

                return
            }

            val intent = Intent(
                this@SignUpActivity,
                VerifyPin::class.java
            )

            intent.putExtra(
                "mobile",
                mobileNumber
            )

            AppUtil.startActivityWithAnimation(
                this@SignUpActivity,
                intent
            )

            finish()

        } catch (e: JSONException) {

            Log.e(
                TAG,
                "Response JSON error",
                e
            )

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Response error",
                e
            )
        }
    }

    override fun onFailed() {

        super.onFailed()

        tst(
            this,
            "Response On failed"
        )
    }

    override fun onBackPressed() {

        val intent = Intent(
            this,
            SignInUpActivity::class.java
        )

        AppUtil.startActivityWithAnimation(
            this,
            intent
        )

        finish()
    }
}