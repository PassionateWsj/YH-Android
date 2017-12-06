package com.intfocus.template.subject.nine.root

import com.intfocus.template.subject.nine.callback.LoadDataCallback
import org.jetbrains.annotations.NotNull

/**
 * @author liuruilin
 * @data 2017/11/1
 * @describe
 */
interface RootPageModel<T> {
    fun getData(@NotNull mParam: String, @NotNull callback: LoadDataCallback<T>)
}
