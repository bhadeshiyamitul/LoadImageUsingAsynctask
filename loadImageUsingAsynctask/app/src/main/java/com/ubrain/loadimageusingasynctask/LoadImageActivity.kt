package com.ubrain.loadimageusingasynctask

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.ImageView
import java.net.HttpURLConnection
import java.net.URL

class LoadImageActivity : AppCompatActivity() {
    private var urlS: String? = "http://api.androidhive.info/images/sample.jpg"
    private var downloadTask: DownloadImagesTask? = null
    private lateinit var mImgDownloadAsync: ImageView
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_image)



        mImgDownloadAsync = findViewById<ImageView>(R.id.imgDownloadAsync)

        mImgDownloadAsync!!.setOnClickListener {
            if (isStoragePermissionGranted()) {
                downloadTask = DownloadImagesTask()
                downloadTask!!.execute(mImgDownloadAsync)
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class DownloadImagesTask : AsyncTask<ImageView, Void, Bitmap>() {

        private var imageView: ImageView? = null

        override fun doInBackground(vararg imageViews: ImageView): Bitmap? {
            this.imageView = imageViews[0]
            return downloadImage(urlS!!)
        }

        override fun onPostExecute(result: Bitmap) {
            imageView!!.setImageBitmap(result)
        }

        private fun downloadImage(url: String): Bitmap? {

            var bmp: Bitmap? = null
            try {
                val ulrN = URL(url)
                var connection = ulrN.openConnection() as HttpURLConnection
                val redirect = connection.getHeaderField("Location")
                if (redirect != null) {
                    connection = URL(redirect).openConnection() as HttpURLConnection
                }
                connection.connect()
                val inputS = connection.inputStream
                bmp = BitmapFactory.decodeStream(inputS)
                if (null != bmp) {
                    return bmp
                }

            } catch (e: Exception) {
            }

            return bmp!!
        }
    }
    private fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 15)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }
    }
}
