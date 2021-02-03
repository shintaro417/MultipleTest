package com.example.multipletest

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.multipletest.databinding.ActivityMainBinding
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val READ_REQUEST_CODE: Int = 42
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ボタンが押されたらギャラリーを開く
        binding.button.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_OPEN_DOCUMENT
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            this.startActivityForResult(Intent.createChooser(intent, "Choose Photo"), READ_REQUEST_CODE)
        }

    }

    //写真が選択された後の動き
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode != RESULT_OK) {
            return
        }
        when (requestCode) {
            READ_REQUEST_CODE -> {
                try {
                    //val filePaths = mutableListOf<String>()
                    val uri = resultData?.data
                    if (uri != null) {
                        //filePaths.add(uri.toString())
                        val inputStream = contentResolver?.openInputStream(uri)
                        val image = BitmapFactory.decodeStream(inputStream)
                        saveImage(image, 999)
                    } else {
                        val clipData = resultData?.clipData
                        val clipItemCount = clipData?.itemCount
                        for (i in 0..clipItemCount!!) {
                            val item: ClipData.Item = clipData.getItemAt(i)
                            val itemUri: Uri = item.uri
                            //filePaths.add(itemUri.toString())
                            val inputStream = contentResolver?.openInputStream(itemUri)
                            val image = BitmapFactory.decodeStream(inputStream)
                            saveImage(image, i)
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun saveImage(bitmap: Bitmap, int: Int) {
        val name: String
        if (int == 999) {
            name = "image.jpg"
        } else {
            name = "image${int}.jpg"
        }
        val byteArrOutputStream = ByteArrayOutputStream()
        val outStream = openFileOutput(name, Context.MODE_PRIVATE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        outStream.write(byteArrOutputStream.toByteArray())
        outStream.close()
    }

    private fun readImage(): Bitmap {
        val bufferedInputStream = BufferedInputStream(openFileInput("image.jpg"))
        return BitmapFactory.decodeStream(bufferedInputStream)
    }
}