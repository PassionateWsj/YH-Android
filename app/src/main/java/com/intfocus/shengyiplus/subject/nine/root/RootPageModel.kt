package com.intfocus.shengyiplus.subject.nine.root

import com.intfocus.shengyiplus.model.callback.LoadDataCallback
import org.jetbrains.annotations.NotNull

/**
 * @author liuruilin
 * @data 2017/11/1
 * @describe
 */
interface RootPageModel<T> {
    fun getData(@NotNull mParam: String, @NotNull callback: LoadDataCallback<T>)
}
