package com.news9Ja

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.fragment_dialog_login.*
import org.json.JSONException
import org.json.JSONObject
import android.text.method.PasswordTransformationMethod



class FragmentDialogLogin: DialogFragment() {
    private var listener: FragmentDialogLoginInteractionListener? = null
    lateinit var thisContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialog_login, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thisContext = activity!!


        btn_Register.setOnClickListener {
            registerProcess()
        }
        btn_Login.setOnClickListener {
            loginProcess()
        }
        clickToLogin.setOnClickListener {
            registerWrapper.visibility = View.GONE
            loginWrapper.visibility = View.VISIBLE
        }
        clickToRegister.setOnClickListener {
            registerWrapper.visibility = View.VISIBLE
            loginWrapper.visibility = View.GONE
        }
        close_dialog.setOnClickListener {
            listener!!.onCloseDialog("close")
        }


        log_password.transformationMethod = PasswordTransformationMethod()
        reg_password_new.transformationMethod = PasswordTransformationMethod()
        reg_password_confirm.transformationMethod = PasswordTransformationMethod()
    }

    private fun registerProcess() {
        val reg_email_address = reg_email_address.text.trim().toString()
        val reg_password_new = reg_password_new.text.trim().toString()
        val reg_password_confirm = reg_password_confirm.text.trim().toString()

        if (reg_email_address ==""||reg_password_new ==""||reg_password_confirm ==""){
            toast("All the fields are required")
        }else if(reg_password_new.length <6){
            toast("Password should not be at least 6 characters")
        }else if(reg_password_new !=reg_password_confirm){
            toast("Passwords don't match")
        }else{

            //creating volley string request
            val dialog= ClassProgressDialog(context)
            dialog.createDialog()
            val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_REGISTER,
                    Response.Listener<String> { response ->
                        dialog.dismissDialog()

                        try {

                            val obj = JSONObject(response)
                            val regStatus = obj.getString("reg_status")
                            if (regStatus == "ok") {
                                toast("Registration successful...")

                                val userDetails = obj.getJSONArray("userDetails").getJSONObject(0)
                                saveUserDetails(userDetails)
                            } else {
                                toast(regStatus)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener { volleyError ->
                        dialog.dismissDialog()
                        ClassAlertDialog(thisContext).toast("ERROR IN NETWORK CONNECTION!")
                    }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["request_type"] = "register"
                    params["reg_email_address"] = reg_email_address
                    params["reg_password_new"] = reg_password_new
                    params["reg_password_confirm"] = reg_password_confirm
                    return params
                }
            }
            //adding request to queue
            VolleySingleton.instance?.addToRequestQueue(stringRequest)
            //volley interactions end
        }
    }
    private fun loginProcess() {
        val log_email_address = log_email_address.text.trim().toString()
        val log_password = log_password.text.trim().toString()

        if (log_email_address ==""||log_password ==""){
            toast("All the fields are required")
        }else if(log_password.length <6){
            toast("Password should not be at least 6 characters")
        }else{

            //creating volley string request
            val dialog= ClassProgressDialog(context)
            dialog.createDialog()
            val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_LOGIN,
                    Response.Listener<String> { response ->
                        dialog.dismissDialog()

                        try {

                            val obj = JSONObject(response)
                            val logStatus = obj.getString("log_status")
                            if (logStatus == "ok") {
                                toast("Logged in successful...")

                                val userDetails = obj.getJSONArray("userDetails").getJSONObject(0)
                                saveUserDetails(userDetails)
                            } else {
                                toast(logStatus)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener { volleyError ->
                        dialog.dismissDialog()
                        ClassAlertDialog(thisContext).toast("ERROR IN NETWORK CONNECTION!")
                    }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["request_type"] = "login"
                    params["log_email_address"] = log_email_address
                    params["log_password"] = log_password
                    return params
                }
            }
            //adding request to queue
            VolleySingleton.instance?.addToRequestQueue(stringRequest)
            //volley interactions end
        }
    }

    fun toast(msg:String){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    //saving user's details
    private fun saveUserDetails(userDetails: JSONObject?) {

        val preference = ClassSharedPreferences(thisContext)
        preference.setUserId(userDetails?.getString("user_id")!!)
        preference.setUserEmail(userDetails.getString("user_email"))
        preference.setUsername(userDetails.getString("user_username"))

        listener?.onCloseDialog("close")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
        } else {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_NoActionBar)
        }

//        val d = dialog
//        if (d != null) {
//            val width = ViewGroup.LayoutParams.MATCH_PARENT
//            val height = ViewGroup.LayoutParams.MATCH_PARENT
//            d.window!!.setLayout(width, height)
//            d.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isCancelable = false
        return dialog
    }


    //Fragment communication with the Home Activity Starts
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentDialogLoginInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface FragmentDialogLoginInteractionListener {
        fun onCloseDialog(input: String)
//        fun onRedirect()
//        fun onGotoCropActivity(input: String)
    }
    //Fragment communication with the Home Activity Stops
}
