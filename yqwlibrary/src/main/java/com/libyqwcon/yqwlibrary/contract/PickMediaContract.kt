package com.libyqwcon.yqwlibrary.contract

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.libyqwcon.yqwlibrary.util.RegisterUtil
import java.util.ArrayList
import java.util.LinkedHashSet

/**
 * 照片、视频选择器
 */
class PickMediaContract(private val maxItems : ()-> Int = {1}) : ActivityResultContract<PickVisualMediaRequest, List<Uri>>() {
    companion object{
        val imageOnly = ActivityResultContracts.PickVisualMedia.ImageOnly
        val videoOnly = ActivityResultContracts.PickVisualMedia.VideoOnly
        val imageAndVideo = ActivityResultContracts.PickVisualMedia.ImageAndVideo
        fun singleMimeType(mimeType: String) =
            ActivityResultContracts.PickVisualMedia.SingleMimeType(mimeType)
    }
    override fun createIntent(
        context: Context,
        input: PickVisualMediaRequest
    ): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            //安卓版本大于33
            Log.e(RegisterUtil.TAG, "createIntent:${maxItems.invoke()}")
            Intent(MediaStore.ACTION_PICK_IMAGES).apply {
                type = getVisualMimeType(input.mediaType)
                require(maxItems.invoke() >= 1) {
                    "The maxItems must be greater than or equal to 1"
                }
                putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, maxItems())
            }
        }else{
            //低版本兼容(大部分手机只能单选)
            val allGranted = getPermission().all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
            require(allGranted) {
                "No relevant permissions obtained!"
            }
            Intent(Intent.ACTION_PICK).apply {
                type = getVisualMimeType(input.mediaType)
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                if (type == null) {
                    // ACTION_OPEN_DOCUMENT requires to set this parameter when launching the
                    // intent with multiple mime types
                    type = "*/*"
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
                }
            }
        }
    }

    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): List<Uri> {
        val resultSet = LinkedHashSet<Uri>()
        intent?.run {
            data?.let { data ->
                resultSet.add(data)
            }
            val clipData = clipData
            if (clipData == null && resultSet.isEmpty()) {
                return emptyList()
            } else if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    if (uri != null) {
                        resultSet.add(uri)
                    }
                }
            }
        }
        return ArrayList(resultSet)
    }

    private fun getVisualMimeType(input: ActivityResultContracts.PickVisualMedia.VisualMediaType): String? {
        return when (input) {
            is ActivityResultContracts.PickVisualMedia.ImageOnly -> "image/*"
            is ActivityResultContracts.PickVisualMedia.VideoOnly -> "video/*"
            is ActivityResultContracts.PickVisualMedia.SingleMimeType -> input.mimeType
            is ActivityResultContracts.PickVisualMedia.ImageAndVideo -> null
        }
    }

    private fun getPermission() : Array<String>{
        return arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}