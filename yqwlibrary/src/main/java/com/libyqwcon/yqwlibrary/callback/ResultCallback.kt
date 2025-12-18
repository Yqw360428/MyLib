package com.libyqwcon.yqwlibrary.callback

import android.net.Uri
import androidx.activity.result.ActivityResult

interface ResultCallback {
    /**
     * 返回的uri列表
     */
    fun onUriResult(uriList : List<Uri>){}
    /**
     * 返回的path列表
     */
    fun onPathResult(pathList : List<String>){}
    /**
     * 同意的权限列表
     */
    fun onPermissionAgree(permissions : List<String>){}
    /**
     * 拒绝的权限列表
     */
    fun onPermissionDeny(permissions: List<String>){}
    /**
     * 权限请求中至少一个拒绝
     */
    fun onPermissionResult(result : Boolean){}
    /**
     * 返回的ActivityResult
     */
    fun onActivityResult(result : ActivityResult){}
    /**
     * 返回拍照的uri
     * 或者视频的uri
     */
    fun onUriListener(uri : Uri?){}
    /**
     * 返回选择的联系人
     */
    fun onPickContact(name : String,numberList : List<String>){}
}