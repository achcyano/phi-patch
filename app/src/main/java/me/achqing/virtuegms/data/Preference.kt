package me.achqing.virtuegms.data

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 工具文件：Preference.kt
 *
 * 提供一组 ReadWriteProperty 实现，方便在实现了 PreferenceModel 的类中使用
 * var foo by preference("foo_key", default)
 *
 * commit 参数：true -> editor.commit()（同步），false -> editor.apply()（异步，推荐）
 */

interface PreferenceModel {

    val context: Context

    val fileName: String
        get() = "${context.packageName}_preferences"

    val prefs: SharedPreferences
        get() = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    fun getPref(path: String = fileName): SharedPreferences {
        return context.getSharedPreferences(path, Context.MODE_PRIVATE)
    }

    // convenience factories (you can also call the delegate constructors directly)
    fun preference(name: String, default: Boolean, commit: Boolean = false, path: String = fileName) =
        BooleanProperty(name, default, commit, path)

    fun preference(name: String, default: Float, commit: Boolean = false, path: String = fileName) =
        FloatProperty(name, default, commit, path)

    fun preference(name: String, default: Int, commit: Boolean = false, path: String = fileName) =
        IntProperty(name, default, commit, path)

    fun preference(name: String, default: Long, commit: Boolean = false, path: String = fileName) =
        LongProperty(name, default, commit, path)

    fun preference(name: String, default: String, commit: Boolean = false, path: String = fileName) =
        StringProperty(name, default, commit, path)

    fun preference(name: String, default: Set<String>, commit: Boolean = false, path: String = fileName) =
        StringSetProperty(name, default, commit, path)

    /**
     * 特殊用法：以 String 存储但在代码中作为 Int 使用
     */
    fun preferenceStrInt(name: String, default: Int, commit: Boolean = false, path: String = fileName) =
        object : ReadWriteProperty<PreferenceModel, Int> {
            private val base = StringProperty(name, default.toString(), commit, path)
            override fun getValue(thisRef: PreferenceModel, property: KProperty<*>): Int =
                base.getValue(thisRef, property).toInt()

            override fun setValue(thisRef: PreferenceModel, property: KProperty<*>, value: Int) =
                base.setValue(thisRef, property, value.toString())
        }
}

/**
 * Base helper to obtain SharedPreferences from the owner (PreferenceModel)
 */
private fun prefsOf(owner: PreferenceModel, path: String): SharedPreferences =
    owner.getPref(path)

/**
 * Boolean delegate
 */
class BooleanProperty(
    private val name: String,
    private val default: Boolean,
    private val commit: Boolean = false,
    private val path: String
) : ReadWriteProperty<PreferenceModel, Boolean> {

    override fun getValue(thisRef: PreferenceModel, property: KProperty<*>): Boolean =
        prefsOf(thisRef, path).getBoolean(name, default)

    override fun setValue(thisRef: PreferenceModel, property: KProperty<*>, value: Boolean) {
        val editor = prefsOf(thisRef, path).edit().putBoolean(name, value)
        if (commit) editor.commit() else editor.apply()
    }
}

/**
 * Int delegate
 */
class IntProperty(
    private val name: String,
    private val default: Int,
    private val commit: Boolean = false,
    private val path: String
) : ReadWriteProperty<PreferenceModel, Int> {

    override fun getValue(thisRef: PreferenceModel, property: KProperty<*>): Int =
        prefsOf(thisRef, path).getInt(name, default)

    override fun setValue(thisRef: PreferenceModel, property: KProperty<*>, value: Int) {
        val editor = prefsOf(thisRef, path).edit().putInt(name, value)
        if (commit) editor.commit() else editor.apply()
    }
}

/**
 * Long delegate
 */
class LongProperty(
    private val name: String,
    private val default: Long,
    private val commit: Boolean = false,
    private val path: String
) : ReadWriteProperty<PreferenceModel, Long> {

    override fun getValue(thisRef: PreferenceModel, property: KProperty<*>): Long =
        prefsOf(thisRef, path).getLong(name, default)

    override fun setValue(thisRef: PreferenceModel, property: KProperty<*>, value: Long) {
        val editor = prefsOf(thisRef, path).edit().putLong(name, value)
        if (commit) editor.commit() else editor.apply()
    }
}

/**
 * Float delegate
 */
class FloatProperty(
    private val name: String,
    private val default: Float,
    private val commit: Boolean = false,
    private val path: String
) : ReadWriteProperty<PreferenceModel, Float> {

    override fun getValue(thisRef: PreferenceModel, property: KProperty<*>): Float =
        prefsOf(thisRef, path).getFloat(name, default)

    override fun setValue(thisRef: PreferenceModel, property: KProperty<*>, value: Float) {
        val editor = prefsOf(thisRef, path).edit().putFloat(name, value)
        if (commit) editor.commit() else editor.apply()
    }
}

/**
 * String delegate
 */
class StringProperty(
    private val name: String,
    private val default: String,
    private val commit: Boolean = false,
    private val path: String
) : ReadWriteProperty<PreferenceModel, String> {

    override fun getValue(thisRef: PreferenceModel, property: KProperty<*>): String =
        prefsOf(thisRef, path).getString(name, default) ?: default

    override fun setValue(thisRef: PreferenceModel, property: KProperty<*>, value: String) {
        val editor = prefsOf(thisRef, path).edit().putString(name, value)
        if (commit) editor.commit() else editor.apply()
    }
}

/**
 * String Set delegate
 */
class StringSetProperty(
    private val name: String,
    private val default: Set<String>,
    private val commit: Boolean = false,
    private val path: String
) : ReadWriteProperty<PreferenceModel, Set<String>> {

    override fun getValue(thisRef: PreferenceModel, property: KProperty<*>): Set<String> =
        prefsOf(thisRef, path).getStringSet(name, default) ?: default

    override fun setValue(thisRef: PreferenceModel, property: KProperty<*>, value: Set<String>) {
        val editor = prefsOf(thisRef, path).edit().putStringSet(name, value)
        if (commit) editor.commit() else editor.apply()
    }
}