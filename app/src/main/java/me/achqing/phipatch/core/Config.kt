package me.achqing.phipatch.core

import android.annotation.SuppressLint
import android.content.Context
import me.achqing.phipatch.data.PreferenceModel


@SuppressLint("StaticFieldLeak")
object Config: PreferenceModel {
    override var context: Context = App.appContext

    var isFirstRun by preference("is_first_run", true)

    var autoCheckUpdate: Boolean by preference("auto_check_updata",true)
    var language: String by preference("language","简体中文")
    var updateChannel: String by preference("updata_channel" ,"稳定版")

}