package com.oguzhan.artbookkotlin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.oguzhan.artbookkotlin.MainActivity.Companion.imageArray
import kotlinx.android.synthetic.main.activity_main2.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.jar.Manifest

class Main2Activity : AppCompatActivity() {

    lateinit var database: SQLiteDatabase
    lateinit var selectedImage: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val intent = intent
        val info = intent.getStringExtra("info")

        if (info.matches("newArt".toRegex())) {
            artNameEditText.setText("")
            artistNameEditText.setText("")
            val selectBitmap =
                BitmapFactory.decodeResource(applicationContext.resources, R.drawable.tap)
            imageView.setImageBitmap(selectBitmap)
            saveButton.visibility = View.VISIBLE
        } else {
            saveButton.visibility = View.INVISIBLE
            val name = intent.getStringExtra("name")
            val artist = intent.getStringExtra("artist")
            val image = imageArray.get(intent.getIntExtra("position", 0))
            artNameEditText.setText(name)
            artistNameEditText.setText(artist)
            imageView.setImageBitmap(image)
        }
    }

    fun selectImage(view: View) {

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 2
            )
        } else {

            intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }

    }

    fun save(view: View) {

        val name = artNameEditText.text.toString()
        val art = artistNameEditText.text.toString()

        val outputStream = ByteArrayOutputStream()
        selectedImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
        val byteArray = outputStream.toByteArray()

        try {

            database = this.openOrCreateDatabase(
                "Arts",
                Context.MODE_PRIVATE,
                null
            )

            database.execSQL("CREATE TABLE IF NOT EXISTS arts (name VARCHAR, artist VARCHAR, image BLOB )")

            val sqlString = "INSERT INTO arts (name, artist, image) VALUES (?, ?, ?)"

            val sqLiteStatement = database.compileStatement(sqlString)
            sqLiteStatement.bindString(1, name)
            sqLiteStatement.bindString(2, art)
            sqLiteStatement.bindBlob(3, byteArray)
            sqLiteStatement.execute()


        } catch (e: Exception) {
            e.printStackTrace()
        }

        intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val imageData = data.data
            try {
                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(this.contentResolver, imageData!!)
                    selectedImage = ImageDecoder.decodeBitmap(source)
                    imageView.setImageBitmap(selectedImage)
                } else {
                    selectedImage =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, imageData)
                    imageView.setImageBitmap(selectedImage)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == 2) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 1)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
