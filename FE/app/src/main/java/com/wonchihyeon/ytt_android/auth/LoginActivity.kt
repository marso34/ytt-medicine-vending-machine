package com.wonchihyeon.ytt_android.auth

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base64
import com.wonchihyeon.ytt_android.MainActivity
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.databinding.ActivityLoginBinding
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        // 로그인에 성공하면 홈프래그먼트로 이동
        binding.login.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 로그인에 성공하면 홈프래그먼트로 이동
        binding.join.setOnClickListener {
            startActivity(intent)
        }

        // 로그인에 성공하면 홈프래그먼트로 이동
        binding.findId.setOnClickListener {
            val intent = Intent(this, FindIdActivity::class.java)
            startActivity(intent)
        }
        // 로그인에 성공하면 홈프래그먼트로 이동
        binding.findPassword.setOnClickListener {
            val intent = Intent(this, FindPasswordActivity::class.java)
            startActivity(intent)
        }

        getHashKey()


    }
    private fun getHashKey() {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (packageInfo == null) Log.e("KeyHash", "KeyHash:null")

    }
}