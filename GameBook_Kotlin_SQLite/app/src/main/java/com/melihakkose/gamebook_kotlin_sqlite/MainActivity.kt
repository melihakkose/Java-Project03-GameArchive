package com.melihakkose.gamebook_kotlin_sqlite

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gameNameList=ArrayList<String>()
        val gameIdList=ArrayList<Int>()

        val arrayAdapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,gameNameList)
        listView.adapter=arrayAdapter


        //VERILERI CEKME
        try{
            val database=this.openOrCreateDatabase("Games",Context.MODE_PRIVATE,null)

            val cursor=database.rawQuery("SELECT * FROM game",null)
            val nameIx=cursor.getColumnIndex("gameName")
            val idIx=cursor.getColumnIndex("id")

            while(cursor.moveToNext()){
                gameNameList.add(cursor.getString(nameIx))
                gameIdList.add(cursor.getInt(idIx))

            }
            cursor.close()
            arrayAdapter.notifyDataSetChanged()

        }catch (e:Exception){
            e.printStackTrace()
        }

        //OLUSTURDUGMUZ LISTEYE TIKLANDIGINDA NE OLACAGINI KODLUYORUZ
        listView.onItemClickListener= AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent=Intent(this,DetailActivity2::class.java)
            intent.putExtra("info","list")
            intent.putExtra("id",gameIdList[position])
            startActivity(intent)
        }
    }

    //OLUSTURULAN MENU BAGLANIYOR
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        //Inflater XML dosyalarini baglamak icin kullanilan kod kismi
        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.add_game,menu)

        return super.onCreateOptionsMenu(menu)
    }

    //MENUDEN SECILEN ITEM ILE NE YAPILACAGINA DAIR KISIM
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Tiklaninca ne olacak?
        if(item.itemId==R.id.add_game_data){
            val intent=Intent(this,DetailActivity2::class.java)
            intent.putExtra("info","menu")
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }



        /*
        VERITABANI KODLARI
        try {

            //Veritabani olusturma
            val myDatabase = this.openOrCreateDatabase("Games", Context.MODE_PRIVATE,null)

            // Veritabani ustunde calistirilacak kod
            //games adli tablo yok ise olustur ve name,releaseDate adlı sutunlar olustur
            myDatabase.execSQL("CREATE TABLE IF NOT EXISTS games (id INTEGER PRIMARY KEY,name VARCHAR, releaseDate INT)")

            //Veri ekleme kodu
            //myDatabase.execSQL("INSERT INTO games (name,releaseDate) VALUES('Red Dead Redemption 2',2018)")
            //myDatabase.execSQL("INSERT INTO games (name,releaseDate) VALUES('Cyberpunk 2077',2020)")
            // myDatabase.execSQL("INSERT INTO games (name,releaseDate) VALUES('Assassins Creed: Valhalla',2020)")

            //UPDATE
            //myDatabase.execSQL("UPDATE games SET name='Baldur s Gate 3' WHERE name='Assassins Creed: Valhalla'")

            //DELETE
            //myDatabase.execSQL("DELETE FROM games WHERE name='Baldur s Gate 3'")

            //Veri cekme -Filtreleme
            //val cursor=myDatabase.rawQuery("SELECT * FROM games WHERE id=2",null)
            val cursor=myDatabase.rawQuery("SELECT * FROM games ",null)

            val nameIx=cursor.getColumnIndex("name")
            val releaseIx=cursor.getColumnIndex("releaseDate")
            val idIx=cursor.getColumnIndex("id")

            while (cursor.moveToNext()){
                println("ID: "+ cursor.getString(idIx))
                println("Name: "+ cursor.getString(nameIx))
                println("Release Date: "+ cursor.getString(releaseIx))
            }
            cursor.close()

        }catch (e: Exception){
            e.printStackTrace()
        }

         */



}