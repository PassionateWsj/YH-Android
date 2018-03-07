package com.intfocus.template.util

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * ****************************************************
 * author jameswong
 * created on: 18/02/02 下午0:14
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
object Base64 {
    @JvmStatic
    fun encodeURIComponent(currentTime: Long): String {
        try {
            val secret = "zz9PChkmmBY-XZ6h8M72dV7AYzGQMv-G"
            val message = "5371896ce7d6c64e9d0720ebb0688481|" + currentTime

            val sha256_HMAC = Mac.getInstance("HmacSHA256")
            val secret_key = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
            sha256_HMAC.init(secret_key)

            val hash = android.util.Base64.encodeToString(sha256_HMAC.doFinal(message.toByteArray()),android.util.Base64.DEFAULT)
            println(hash)
            return hash
        } catch (e: Exception) {
            println("Error")
        }
        return ""
    }
}
