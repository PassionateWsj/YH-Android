package com.intfocus.hx.business.scanner

/**
 * Created by liuruilin on 2017/6/30.
 */
class ScannerRequest(var isSuccess: Boolean, var stateCode: Int) {
    var htmlPath = ""
    var errorInfo = ""
}