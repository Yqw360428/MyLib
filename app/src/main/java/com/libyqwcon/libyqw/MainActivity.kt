package com.libyqwcon.libyqw

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.libyqwcon.yqwlibrary.contract.PickMediaContract
import com.libyqwcon.yqwlibrary.util.RegisterUtil
import com.libyqwcon.yqwlibrary.util.RegisterUtil.TAG
import com.libyqwcon.yqwlibrary.callback.ResultCallback

class MainActivity : AppCompatActivity() {
    private val pickLaunch = RegisterUtil.registerPickMedia(this)
    private val permissionLaunch = RegisterUtil.registerPermissions(this)
    private val forResultLaunch = RegisterUtil.registerForResult(this)
    private val takePictureLaunch = RegisterUtil.registerTakePicture(this)
    private val captureLaunch = RegisterUtil.registerCaptureVideo(this)
    private val pickContactLaunch = RegisterUtil.registerPickContact(this)
    private val getContentsLaunch = RegisterUtil.registerOpenMultipleDocuments(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top,0, systemBars.bottom)
            insets
        }
        findViewById<Button>(R.id.permission).setOnClickListener {
            permissionLaunch.launch(RegisterUtil.permissionsRequest(getPermission(),object : ResultCallback{
                override fun onPermissionResult(result: Boolean) {
                    super.onPermissionResult(result)
                    Log.e(TAG, "onPermissionResult:$result")
                }
            }))
        }

        findViewById<Button>(R.id.pick1).setOnClickListener {
            pickLaunch.launch(RegisterUtil.pickMediaRequest(PickMediaContract.imageOnly, resultListener = object : ResultCallback{
                override fun onUriResult(uriList: List<Uri>) {
                    super.onUriResult(uriList)
                    Log.e(TAG, "registerPickUri:$uriList")
                }
                override fun onPathResult(pathList: List<String>) {
                    super.onPathResult(pathList)
                    Log.e(TAG, "registerPickPath:$pathList")
                }
            }))
        }

        findViewById<Button>(R.id.pick5).setOnClickListener {
            pickLaunch.launch(RegisterUtil.pickMediaRequest(PickMediaContract.imageOnly,5,object : ResultCallback{
                override fun onUriResult(uriList: List<Uri>) {
                    super.onUriResult(uriList)
                    Log.e(TAG, "registerPick:$uriList")
                }
                override fun onPathResult(pathList: List<String>) {
                    super.onPathResult(pathList)
                    Log.e(TAG, "registerPick:$pathList")
                }
            }))
        }

        findViewById<Button>(R.id.pick10).setOnClickListener {
            pickLaunch.launch(RegisterUtil.pickMediaRequest(PickMediaContract.imageOnly,10,object : ResultCallback{
                override fun onUriResult(uriList: List<Uri>) {
                    super.onUriResult(uriList)
                    Log.e(TAG, "registerPick:$uriList")
                }
                override fun onPathResult(pathList: List<String>) {
                    super.onPathResult(pathList)
                    Log.e(TAG, "registerPick:$pathList")
                }
            }))
        }

        findViewById<Button>(R.id.start).setOnClickListener {
            forResultLaunch.launch(RegisterUtil.forResultRequest(this, AActivity::class.java,object : ResultCallback{
                override fun onActivityResult(result: ActivityResult) {
                    super.onActivityResult(result)
                    Log.e(TAG, "onActivityResult:${result.resultCode},${result.data?.extras}")
                }
            }))
        }

        findViewById<Button>(R.id.take).setOnClickListener {
            takePictureLaunch.launch(RegisterUtil.takePictureRequest(this,"${System.currentTimeMillis()}.jpg",object : ResultCallback{
                override fun onUriListener(uri: Uri?) {
                    super.onUriListener(uri)
                    Log.e(TAG, "onTakePicture:$uri")
                }
            }))
        }

        findViewById<Button>(R.id.capture).setOnClickListener {
            captureLaunch.launch(RegisterUtil.captureVideoRequest(this,"${System.currentTimeMillis()}.mp4",object : ResultCallback{
                override fun onUriListener(uri: Uri?) {
                    super.onUriListener(uri)
                    Log.e(TAG, "onCaptureVideo:$uri")
                }
            }))
        }

        findViewById<Button>(R.id.contact).setOnClickListener {
            permissionLaunch.launch(RegisterUtil.permissionsRequest(arrayOf(Manifest.permission.READ_CONTACTS),object : ResultCallback{
                override fun onPermissionResult(result: Boolean) {
                    super.onPermissionResult(result)
                    if (result){
                        pickContactLaunch.launch(RegisterUtil.pickContactRequest(object : ResultCallback{
                            override fun onPickContact(name: String, numberList: List<String>) {
                                super.onPickContact(name, numberList)
                                Log.e(TAG, "onPickContact:$name,$numberList,${numberList[0].length}")
                            }
                        }))
                    }
                }
            }))
        }

        findViewById<Button>(R.id.get).setOnClickListener {
            getContentsLaunch.launch(RegisterUtil.getOpenMultipleDocuments(arrayOf("image/*"),object : ResultCallback{
                override fun onUriResult(uriList: List<Uri>) {
                    super.onUriResult(uriList)
                    Log.e(TAG, "onUriResult:$uriList")
                }
            }))
        }
    }

    private fun getPermission() : Array<String>{
        return arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}