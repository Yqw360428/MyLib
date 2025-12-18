package com.libyqwcon.yqwlibrary.util

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.ContactsContract
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.telephony.PhoneNumberUtils
import androidx.activity.ComponentActivity
import androidx.core.net.toUri


object UriUtil {
    /**
     * 获取文件的路径
     */
    fun getPathFromURI(componentActivity: ComponentActivity, uri: Uri?): String {
        var realPath = ""
        runCatching {
            if (DocumentsContract.isDocumentUri(componentActivity, uri)) {
                val documentId = DocumentsContract.getDocumentId(uri)
                when(uri?.authority){
                    "com.android.externalstorage.documents"->{
                        val split = documentId.split(":")
                        val type = split[0]
                        if (type.equals("primary", ignoreCase = true)) {
                            realPath = "${Environment.getExternalStorageDirectory()}/${split[1]}"
                        }
                    }
                    "com.android.providers.downloads.documents"->{
                        val contentUri = ContentUris.withAppendedId(
                            "content://downloads/public_downloads".toUri(),
                            documentId.toLong()
                        )
                        realPath = getDataColumn(componentActivity, contentUri, null, null).toString()
                    }
                    "com.android.providers.media.documents"->{
                        val split = documentId.split(":")
                        val contentUri = when (split[0]) {
                            "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                            else -> null
                        }
                        val selection = "_id=?"
                        val selectionArgs = arrayOf(split[1])
                        realPath = getDataColumn(componentActivity, contentUri, selection, selectionArgs).toString()
                    }
                }
            } else if ("content".equals(uri?.scheme, ignoreCase = true)) {
                realPath = getDataColumn(componentActivity, uri, null, null).toString()
            } else if ("file".equals(uri?.scheme, ignoreCase = true)) {
                realPath = uri?.path.toString()
            }
        }
        return realPath
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri ?: return null, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun handlePickedContact(componentActivity: ComponentActivity,contactUri: Uri?) : Pair<String, List<String>> {
        if (contactUri == null) return Pair("",emptyList())
        val resolver = componentActivity.contentResolver

        // 1️⃣ 查询联系人ID和姓名
        val cursor = resolver.query(
            contactUri,
            arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
            ),
            null,
            null,
            null
        )

        if (cursor == null || !cursor.moveToFirst()) return Pair("",emptyList())

        val contactId = cursor.getString(
            cursor.getColumnIndex(ContactsContract.Contacts._ID)
        )
        val contactName = cursor.getString(
            cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
        )
        cursor.close()

        // 2️⃣ 查询电话
        val phoneCursor = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
            ),
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
            arrayOf<String?>(contactId),
            null
        )

        val phoneNumbers: MutableList<String> = ArrayList()
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                val number = phoneCursor.getString(
                    phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                )
                phoneNumbers.add(number)
            }
            phoneCursor.close()
        }

        // 3️⃣ 输出或者弹窗让用户选择
        return Pair(contactName,phoneNumbers.map {
            PhoneNumberUtils.normalizeNumber(it)
        })
    }


}