package com.libyqwcon.yqwlibrary.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageAndVideo
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.VisualMediaType
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.libyqwcon.yqwlibrary.callback.ResultCallback
import com.libyqwcon.yqwlibrary.contract.PickMediaContract
import java.io.File

object RegisterUtil {
    const val TAG = "yqw=====>"
    private var maxItems = 1//选择数量
    private var resultListener: ResultCallback? = null
    private var uri: Uri? = null

    /**
     * 选择照片和视频协定
     */
    fun registerPickMedia(componentActivity: ComponentActivity) =
        componentActivity.registerForActivityResult(PickMediaContract { maxItems }) {
            resultListener?.onUriResult(it)
            resultListener?.onPathResult(it.map { uri ->
                UriUtil.getPathFromURI(
                    componentActivity,
                    uri
                )
            })
        }

    /**
     * 选择照片和视频的回调
     */
    fun pickMediaRequest(
        mediaType: VisualMediaType = ImageAndVideo,
        maxItems: Int = 1,
        resultListener: ResultCallback = object : ResultCallback {}
    ): PickVisualMediaRequest {
        this@RegisterUtil.maxItems = maxItems
        this.resultListener = resultListener
        return PickVisualMediaRequest.Builder().setMediaType(mediaType).build()
    }


    /**
     * 请求权限的协定
     */
    fun registerPermissions(
        componentActivity: ComponentActivity
    ) = componentActivity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        resultListener?.onPermissionResult(it.all { value -> value.value })
        resultListener?.onPermissionAgree(it.filterValues { value -> value }.keys.toList())
        resultListener?.onPermissionDeny(it.filterValues { value -> !value }.keys.toList())
    }

    /**
     * 请求权限回调
     */
    fun permissionsRequest(
        permissions: Array<String>,
        resultListener: ResultCallback = object : ResultCallback {}
    ): Array<String> {
        this.resultListener = resultListener
        return permissions
    }


    /**
     * 带参回传的协定
     */
    fun registerForResult(
        componentActivity: ComponentActivity
    ) =
        componentActivity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            resultListener?.onActivityResult(it)
        }

    /**
     * 带参回传的协定
     */
    fun forResultRequest(
        context: Context,
        activity: Class<*>,
        resultListener: ResultCallback = object : ResultCallback {}
    ): Intent {
        this.resultListener = resultListener
        return Intent(context, activity)
    }

    /**
     * 系统拍照协定
     */
    fun registerTakePicture(
        componentActivity: ComponentActivity
    ) = componentActivity.registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            resultListener?.onUriListener(uri)
        } else {
            resultListener?.onUriListener(null)
        }
        uri = null
    }

    /**
     * 系统拍照请求
     */
    fun takePictureRequest(
        context: Context,
        name: String,
        resultListener: ResultCallback = object : ResultCallback {}
    ): Uri {
        uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            File(context.cacheDir, name)
        )
        this.resultListener = resultListener
        return uri!!
    }

    /**
     * 系统录制视频协定
     */
    fun registerCaptureVideo(
        componentActivity: ComponentActivity
    ) = componentActivity.registerForActivityResult(ActivityResultContracts.CaptureVideo()) {
        if (it) {
            resultListener?.onUriListener(uri)
        } else {
            resultListener?.onUriListener(null)
        }
        uri = null
    }

    /**
     * 系统录制视频请求
     */
    fun captureVideoRequest(
        context: Context,
        name: String,
        resultListener: ResultCallback = object : ResultCallback {}
    ): Uri {
        uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            File(context.cacheDir, name)
        )
        this.resultListener = resultListener
        return uri!!
    }

    /**
     * 选择联系人协定
     */
    fun registerPickContact(
        componentActivity: ComponentActivity
    ) = componentActivity.registerForActivityResult(ActivityResultContracts.PickContact()) {
        val granted = ContextCompat.checkSelfPermission(
            componentActivity,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        require(granted) {
            "No relevant permissions obtained!"
        }
        val (name, list) = UriUtil.handlePickedContact(componentActivity, it)
        resultListener?.onPickContact(name, list)
    }

    /**
     * 选择联系人请求
     */
    fun pickContactRequest(
        resultListener: ResultCallback = object : ResultCallback {}
    ): Void? {
        this.resultListener = resultListener
        return null
    }

    /**
     * 获取内容协定
     */
    fun registerOpenMultipleDocuments(
        componentActivity: ComponentActivity
    ) = componentActivity.registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) {
        resultListener?.onUriResult(it)
    }

    /**
     * 获取内容请求
     */
    fun getOpenMultipleDocuments(
        mimeTypes : Array<String>,
        resultListener: ResultCallback = object : ResultCallback {}
    ) : Array<String>{
        this.resultListener = resultListener
        return mimeTypes
    }
}