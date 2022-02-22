package com.lanshifu.oommonitor

/**
 * @author lanxiaobin
 * @date 2022/2/23
 */
internal class OOMMonitor {
    init {
        System.loadLibrary("native-lib")
    }

    external fun appendString(s1: String,s2: String):String
}