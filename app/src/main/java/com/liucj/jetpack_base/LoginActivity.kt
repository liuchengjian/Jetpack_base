package com.liucj.jetpack_base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.liucj.jetpack_base.api.AccountApi
import com.liucj.jetpack_base.api.ApiFactory
import com.liucj.jetpack_base.databinding.ActivityLoginBinding
import com.liucj.lib_common.utils.SPUtil
import com.liucj.lib_common.utils.StatusBarKt
import com.liucj.lib_network.restful_kt.HiCallback
import com.liucj.lib_network.restful_kt.HiResponse

class LoginActivity: AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        StatusBar.fitSystemBar(this)
        StatusBarKt.setStatusBar(this, true, translucent = false)
//        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.actionBack.setOnClickListener { onBackPressed() }
        binding.actionRegister.setOnClickListener { goRegistration() }
        binding.actionLogin.setOnClickListener { goLogin() }
    }

    private fun goLogin() {
        val name = binding.inputItemUsername.getEditText().text
        val password = binding.inputItemPassword.getEditText().text

        if (TextUtils.isEmpty(name) || (TextUtils.isEmpty(password))) {
            return
        }

        //viewmodel +respostory +livedata
        ApiFactory.create(AccountApi::class.java).login(name.toString(), password.toString())
                .enqueue(object : HiCallback<String> {
                    override fun onSuccess(response: HiResponse<String>) {
                        if (response.code == HiResponse.SUCCESS) {
//                            showToast(getString(R.string.login_success))

                            //usernmanager
                            val data = response.data
                            SPUtil.putString("boarding-pass", data!!)
                            setResult(Activity.RESULT_OK, Intent())
                            finish()
                        } else {
//                            showToast(getString(R.string.login_failed) + response.msg)
                        }
                    }

                    override fun onFailed(throwable: Throwable) {
//                        showToast(getString(R.string.login_failed) + throwable.message)
                    }

                })
    }

    private fun goRegistration() {

    }
}