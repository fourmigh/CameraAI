/*
 *    Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.caojun.cameracolor.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
//import com.android.volley.RequestQueue
//import com.android.volley.Response
//import com.android.volley.toolbox.StringRequest
//import com.android.volley.toolbox.Volley
//import com.google.gson.Gson
import com.wonderkiln.camerakit.CameraKitError
import com.wonderkiln.camerakit.CameraKitEvent
import com.wonderkiln.camerakit.CameraKitEventListener
import com.wonderkiln.camerakit.CameraKitImage
import com.wonderkiln.camerakit.CameraKitVideo
import kotlinx.android.synthetic.main.activity_tensorflow.*
import org.caojun.cameracolor.Constant
import org.caojun.cameracolor.R
import org.caojun.cameracolor.tensorflow.Classifier
import org.caojun.cameracolor.tensorflow.TensorFlowImageClassifier
//import org.caojun.cameracolor.tensorflow.TranslateResult
//import org.jetbrains.anko.doAsync
//import org.jetbrains.anko.uiThread
//import java.io.UnsupportedEncodingException
//import java.net.URLEncoder
//import java.security.MessageDigest
//import java.security.NoSuchAlgorithmException
//import java.util.*
import java.util.concurrent.Executors

@Route(path = Constant.ACTIVITY_TENSORFLOR)
class TensorflowActivity : AppCompatActivity() {

    private lateinit var classifier: Classifier
    private val executor = Executors.newSingleThreadExecutor()

//    private lateinit var mRequestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tensorflow)

        textViewResult.movementMethod = ScrollingMovementMethod()

        cameraView.addCameraKitListener(object : CameraKitEventListener {
            override fun onEvent(cameraKitEvent: CameraKitEvent) {

            }

            override fun onError(cameraKitError: CameraKitError) {

            }

            override fun onImage(cameraKitImage: CameraKitImage) {

                var bitmap = cameraKitImage.bitmap

                bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false)

                imageViewResult.setImageBitmap(bitmap)

                val results = classifier.recognizeImage(bitmap)

                textViewResult.text = results.toString()

//                doAsync {
//                    translation(textViewResult.text.toString())
//                }
            }

            override fun onVideo(cameraKitVideo: CameraKitVideo) {

            }
        })

        btnToggleCamera.setOnClickListener { cameraView.toggleFacing() }

        btnDetectObject.setOnClickListener { cameraView.captureImage() }

        initTensorFlowAndLoadModel()

//        mRequestQueue = Volley.newRequestQueue(this)
    }

    override fun onResume() {
        super.onResume()
        cameraView.start()
    }

    override fun onPause() {
        cameraView.stop()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.execute { classifier.close() }
    }

    private fun initTensorFlowAndLoadModel() {
        executor.execute {
            try {
                classifier = TensorFlowImageClassifier.create(
                        assets,
                        MODEL_FILE,
                        LABEL_FILE,
                        INPUT_SIZE,
                        IMAGE_MEAN,
                        IMAGE_STD,
                        INPUT_NAME,
                        OUTPUT_NAME)
                makeButtonVisible()
            } catch (e: Exception) {
                throw RuntimeException("Error initializing TensorFlow!", e)
            }
        }
    }

    private fun makeButtonVisible() {
        runOnUiThread { btnDetectObject.visibility = View.VISIBLE }
    }

    companion object {

        private val INPUT_SIZE = 224
        private val IMAGE_MEAN = 117
        private val IMAGE_STD = 1f
        private val INPUT_NAME = "input"
        private val OUTPUT_NAME = "output"

        private val MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb"
        private val LABEL_FILE = "file:///android_asset/imagenet_comp_graph_label_cn.txt"

//        private val TRANSLATION_URL = "http://api.fanyi.baidu.com/api/trans/vip/translate"
    }

//    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
//    private fun translation(input: String) {
//        var input = input
//        var appid = "20160920000028985"
//        val key = "qy53qCdsTFBRCGwNTsxo"
//        val salt = Random().nextInt(10000)
//        val result = getMD5(input, appid, key, salt)
//        appid = urlEncode(appid)
//        input = urlEncode(input)
//        val strSalt = urlEncode(Integer.toString(salt))
//        val from = urlEncode("en")
//        val to = urlEncode("zh")
//        val url = (TRANSLATION_URL + "?q=" + input + "&from=" + from + "&to=" + to + "&appid="
//                + appid + "&salt=" + strSalt + "&sign=" + result)
//        val stringRequest = StringRequest(url, Response.Listener { response ->
//            doAsync {
//
//                val result = Gson().fromJson(response, TranslateResult::class.java)
//                val dst = result.transResult[0].dst
//                uiThread {
//                    textViewResult.text = "${textViewResult.text}\n$dst"
//                }
//            }
//        }, Response.ErrorListener { })
//        mRequestQueue.add(stringRequest)
//    }
//
//    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
//    private fun getMD5(input: String, appid: String, key: String, salt: Int): String {
//        val messageDigest = MessageDigest.getInstance("MD5")
//        messageDigest.reset()
//        val s = appid + input + salt + key
//        messageDigest.update(s.toByteArray())
//        val digest = messageDigest.digest()
//        val md5StrBuilder = StringBuilder()
//
//        //将加密后的byte数组转换为十六进制的字符串,否则的话生成的字符串会乱码
//        for (i in digest.indices) {
//            val iDigest = 0xFF and digest[i].toInt()
//            if (Integer.toHexString(iDigest).length == 1) {
//                md5StrBuilder.append("0").append(
//                        Integer.toHexString(iDigest))
//            } else {
//                md5StrBuilder.append(Integer.toHexString(iDigest))
//            }
//        }
//        return md5StrBuilder.toString()
//    }
//
//    @Throws(UnsupportedEncodingException::class)
//    private fun urlEncode(s: String): String {
//        return URLEncoder.encode(s, "UTF-8")
//    }
}
