package com.oguzhan.artbookkotlin

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    lateinit var arrayAdapter: ArrayAdapter<String>
    lateinit var nameArray: ArrayList<String>
    lateinit var artistArray: ArrayList<String>


    companion object {
        lateinit var imageArray: ArrayList<Bitmap>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nameArray = ArrayList()
        artistArray = ArrayList()
        imageArray = ArrayList()

        arrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_expandable_list_item_1,
            nameArray
        )
        listView.adapter = arrayAdapter

        listView.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l ->
                val intent = Intent(applicationContext, Main2Activity::class.java)
                intent.putExtra("info", "oldArt")
                intent.putExtra("name", nameArray.get(i))
                intent.putExtra("artist", artistArray.get(i))
                intent.putExtra("position", i)
                startActivity(intent)
            }

        getData()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.add_art, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_art) {

            intent = Intent(applicationContext, Main2Activity::class.java)
            intent.putExtra("info", "newArt")
            startActivity(intent)

        }
        return super.onOptionsItemSelected(item)
    }

    fun getData() {

        val database: SQLiteDatabase = this.openOrCreateDatabase("Arts", Context.MODE_PRIVATE, null)

        database.execSQL("CREATE TABLE IF NOT EXISTS arts (name VARVHAR, artist VARCHAR, image BLOB)")

        val cursor: Cursor = database.rawQuery("SELECT*FROM arts", null)

        val nameIx = cursor.getColumnIndex("name")
        val artistIx = cursor.getColumnIndex("artist")
        val imageIx = cursor.getColumnIndex("image")

        while (cursor.moveToNext()) {

            nameArray.add(cursor.getString(nameIx))
            artistArray.add(cursor.getString(artistIx))

            val byteArray = cursor.getBlob(imageIx)
            val image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            imageArray.add(image)

            arrayAdapter.notifyDataSetChanged()


        }

        cursor.close()

    }
}
