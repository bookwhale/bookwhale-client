package com.example.bookwhale.data.preference

import android.content.Context
import android.content.SharedPreferences

class MyPreferenceManager(
    private val context: Context
) {
    companion object {
        const val PREFERENCES_NAME = "bookwhale-pref"
        private const val DEFAULT_VALUE_STRING = ""
        private const val DEFAULT_VALUE_BOOLEAN = false
        private const val DEFAULT_VALUE_INT = -1
        private const val DEFAULT_VALUE_LONG = -1L
        private const val DEFAULT_VALUE_FLOAT = -1f

        const val EMAIL = "EMAIL"
        const val ID = "ID"
        const val NAME = "NAME"
        const val KEY_ID_TOKEN = "ID_TOKEN"
        const val KEY_ACCESS_TOKEN = "ACCESS_TOKEN"
        const val KEY_REFRESH_TOKEN = "REFRESH_TOKEN"
        const val KEY_DEVICE_TOKEN = "DEVICE_TOKEN"
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    private val prefs by lazy { getPreferences(context) }

    private val editor by lazy { prefs.edit() }


    /**
     * String 값 저장
     * @param context
     * @param key
     * @param value
     */
    fun setString(key: String?, value: String?) {
        editor.putString(key, value)
        editor.apply()
    }

    /**
     * boolean 값 저장
     * @param context
     * @param key
     * @param value
     */
    fun setBoolean(key: String?, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    /**
     * int 값 저장
     * @param context
     * @param key
     * @param value
     */
    fun setInt(key: String?, value: Int) {
        editor.putInt(key, value)
        editor.apply()
    }

    /**
     * long 값 저장
     * @param context
     * @param key
     * @param value
     */
    fun setLong(key: String?, value: Long) {
        editor.putLong(key, value)
        editor.apply()
    }

    /**
     * float 값 저장
     * @param context
     * @param key
     * @param value
     */
    fun setFloat(key: String?, value: Float) {
        editor.putFloat(key, value)
        editor.apply()
    }

    /**
     * String 값 로드
     * @param context
     * @param key
     * @return
     */
    fun getString(key: String?): String? {
        return prefs.getString(key, DEFAULT_VALUE_STRING)
    }

    /**
     * boolean 값 로드
     * @param context
     * @param key
     * @return
     */
    fun getBoolean(key: String?): Boolean {
        return prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN)
    }

    /**
     * int 값 로드
     * @param context
     * @param key
     * @return
     */
    fun getInt(key: String?): Int {
        return prefs.getInt(key, DEFAULT_VALUE_INT)
    }

    /**
     * long 값 로드
     * @param context
     * @param key
     * @return
     */
    fun getLong(key: String?): Long {
        return prefs.getLong(key, DEFAULT_VALUE_LONG)
    }

    /**
     * float 값 로드
     * @param context
     * @param key
     * @return
     */
    fun getFloat(key: String?): Float {
        return prefs.getFloat(key, DEFAULT_VALUE_FLOAT)
    }

    /**
     * 키 값 삭제
     * @param context
     * @param key
     */
    fun removeKey(key: String?) {
        editor.remove(key)
        editor.apply()
    }

    /**
     * 모든 저장 데이터 삭제
     * @param context
     */
    fun clear() {
        editor.clear()
        editor.apply()
    }

    fun putEmail(email: String) {
        editor.putString(EMAIL, email)
        editor.apply()
    }

    fun getEmail(): String? {
        return prefs.getString(EMAIL, null)
    }

    fun removeEmail() {
        editor.putString(EMAIL, null)
        editor.apply()
    }

    fun putIdToken(idToken: String) {
        editor.putString(KEY_ID_TOKEN, idToken)
        editor.apply()
    }

    fun getIdToken(): String? {
        return prefs.getString(KEY_ID_TOKEN, null)
    }

    fun removeIdToken() {
        editor.putString(KEY_ID_TOKEN, null)
        editor.apply()
    }

    fun putAccessToken(idToken: String) {
        editor.putString(KEY_ACCESS_TOKEN, idToken)
        editor.apply()
    }

    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun removeAccessToken() {
        editor.putString(KEY_ACCESS_TOKEN, null)
        editor.apply()
    }

    fun putRefreshToken(idToken: String) {
        editor.putString(KEY_REFRESH_TOKEN, idToken)
        editor.apply()
    }

    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    fun removeRefreshToken() {
        editor.putString(KEY_REFRESH_TOKEN, null)
        editor.apply()
    }

    fun putDeviceToken(idToken: String) {
        editor.putString(KEY_DEVICE_TOKEN, idToken)
        editor.apply()
    }

    fun getDeviceToken(): String? {
        return prefs.getString(KEY_DEVICE_TOKEN, null)
    }

    fun removeDeviceToken() {
        editor.putString(KEY_DEVICE_TOKEN, null)
        editor.apply()
    }

    fun putId(id: Int) {
        editor.putInt(ID, id)
        editor.apply()
    }

    fun getId(): Int {
        return prefs.getInt(ID, -1)
    }

    fun removeId() {
        editor.putInt(ID, -1)
        editor.apply()
    }

    fun putName(id: String) {
        editor.putString(NAME, id)
        editor.apply()
    }

    fun getName(): String? {
        return prefs.getString(NAME, null)
    }

    fun removeName() {
        editor.putString(NAME, null)
        editor.apply()
    }

}