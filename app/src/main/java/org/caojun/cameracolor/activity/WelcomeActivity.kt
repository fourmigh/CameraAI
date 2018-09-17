package org.caojun.cameracolor.activity

import android.Manifest
import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.android.synthetic.main.activity_welcome.*
import org.caojun.activity.BaseActivity
import org.caojun.cameracolor.Constant
import org.caojun.cameracolor.R
import org.caojun.utils.ActivityUtils

/**
 * Created by CaoJun on 2018-3-23.
 */

class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        //        this.checkSelfPermission(Manifest.permission.CAMERA, new ActivityUtils.RequestPermissionListener() {
        //
        //            @Override
        //            public void onFail() {
        //                finish();
        //            }
        //
        //            @Override
        //            public void onSuccess() {
        //                ARouter.getInstance().build(Constant.ACTIVITY_MAIN).navigation();
        //                finish();
        //            }
        //        });

        btnColor.setOnClickListener {
            gotoActivity(true)
        }

        btnPicture.setOnClickListener {
            gotoActivity(false)
        }
    }

    private fun gotoActivity(isColor: Boolean) {
        this.checkSelfPermission(Manifest.permission.CAMERA, object : ActivityUtils.RequestPermissionListener {

            override fun onFail() {
            }

            override fun onSuccess() {
                if (isColor) {
                    ARouter.getInstance().build(Constant.ACTIVITY_MAIN).navigation()
                } else {
                    ARouter.getInstance().build(Constant.ACTIVITY_TENSORFLOR).navigation()
                }
            }
        });
    }
}
