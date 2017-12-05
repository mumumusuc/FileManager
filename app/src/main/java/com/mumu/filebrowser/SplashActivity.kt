package com.mumu.filebrowser

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val state1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val state2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Log.d("SplashActivity", "checkSelfPermission : $state1 | $state2")
        if (state1 != PERMISSION_GRANTED || state2 != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    111)
        } else {
            /*start main activity*/
            startMainActivity()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 111) {
            var result = true
            grantResults.forEach {
                result = result and (it == PERMISSION_GRANTED)
            }
            if (result) {
                startMainActivity()
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent()
        intent.component = ComponentName(this, "com.mumu.filebrowser.views.impl.MainViewActivity")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}