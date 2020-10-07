package com.melihakkose.gamebook_kotlin_sqlite

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail2.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.security.Permission
import java.util.jar.Manifest

class DetailActivity2 : AppCompatActivity() {

    var selectedImage: Uri?=null
    var selectedBitmap:Bitmap?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail2)

        val intent= intent
        val info=intent.getStringExtra("info")

        //MENUDEN MI GIRIS YAPILDI (ADD GAME) YOKSA LISTEDEN MI ITEM SECILDI
        if(info.equals("menu")){
            editTextGame.setText("")
            editTextDeveloper.setText("")
            editTextReleaseDate.setText("")
            button.visibility=View.VISIBLE
            val selectedImageBackground=BitmapFactory.decodeResource(applicationContext.resources,R.drawable.selectedimage)
            imageView.setImageBitmap(selectedImageBackground)
        }else{
            button.visibility=View.INVISIBLE
            editTextDeveloper.isEnabled=false
            editTextGame.isEnabled=false
            editTextReleaseDate.isEnabled=false

            val selectedId=intent.getIntExtra("id",1)

            //LISTEDEN CEKILEN VERILERI GEREKLI YERLERE ILISTIRMEMIZ GEREKLI
            //VERITABANINA ERISECEGIZ
            try{
                val database=this.openOrCreateDatabase("Games",Context.MODE_PRIVATE,null)
                val cursor=database.rawQuery("SELECT * FROM game WHERE id=?", arrayOf(selectedId.toString()))
                val idIx=cursor.getColumnIndex("id")
                val nameIx=cursor.getColumnIndex("gameName")
                val devIx=cursor.getColumnIndex("developer")
                val releaseIx=cursor.getColumnIndex("releaseDate")
                val imageIx=cursor.getColumnIndex("image")


                while(cursor.moveToNext()){
                    editTextReleaseDate.setText(cursor.getString(releaseIx))
                    editTextDeveloper.setText(cursor.getString(devIx))
                    editTextGame.setText(cursor.getString(nameIx))

                    val byteArray=cursor.getBlob(imageIx)
                    val bitmap=BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                    imageView.setImageBitmap(bitmap)

                }


            }catch (e:Exception){
                e.printStackTrace()
            }

        }
    }

    fun selectImage(view: View){

        //TIKLANDIGI ZAMAN IZIN VAR MI YOK MU DIYE KONTROL EDILMELI

        //IZIN VERILMEDI
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            //IZIN ISTEYECEGIZ
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)


            //IZIN VERILDIYSE
        }else{
            //Galeriye gitmek icin yapilan intent (URI -> Gorselin adresi)
            val intentGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intentGallery,2)

        }

    }
    fun saveData(view:View){

        //VERITABANINA KAYDETMEK
        val gameName=editTextGame.text.toString()
        val releaseDate=editTextReleaseDate.text.toString().toIntOrNull()
        val devGame=editTextDeveloper.text.toString()


        if(selectedBitmap!=null){
            //RESMIN BOYUTUNU AYARLAMA
            val smallBİtmap=scaleImage(selectedBitmap!!,300)
            //OUTPUTSTREAM compress icin gerekli
            val outpuStream=ByteArrayOutputStream()

            //IMAGEVIEW' i VERI OLARAK ALMA (BU ISLEM BITMAP UZERINDEN YAPILIR)
            smallBİtmap.compress(Bitmap.CompressFormat.PNG,50,outpuStream)

            //SECILEN RESIM BYTE DIZISINE DONUSTURULUYOR
            val byteArray=outpuStream.toByteArray()

            try {

                //DATABASE KODLARI
                val database = this.openOrCreateDatabase("Games", Context.MODE_PRIVATE, null)
                //VERITABANI OLUSTUR
                database.execSQL("CREATE TABLE IF NOT EXISTS game(id INTEGER PRIMARY KEY, gameName VARCHAR, releaseDate INTEGER, developer VARCHAR, image BLOB)")
                //VERİ EKLEME kodu
                val sqlString =
                    "INSERT INTO game (gameName,releaseDate,developer,image) VALUES (?,?,?,?)"
                //COMPILE EDILECEK KOD
                val statement = database.compileStatement(sqlString)
                //VERILERI BIND ETME
                statement.bindString(1, gameName)
                statement.bindString(2, releaseDate.toString())
                statement.bindString(3, devGame)
                statement.bindBlob(4, byteArray)
                statement.execute()
            }catch (e:Exception){
                e.printStackTrace()
            }
           // finish()
            //LISTVIEW GUNCELLEYEREK DONMEK ICIN INTENT ILE GIDECEGIZ
            val intent=Intent(this,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)



        }else{
            Toast.makeText(this,"Image not Selected!",Toast.LENGTH_LONG).show()
        }

    }

    //RESMIN BOYUTUNU KUCULTEN BITMAP FONKSIYONU
    fun scaleImage(image:Bitmap,maximumSize:Int):Bitmap{

        //RESMIN BOYUTLARINI ALMAK ICIN
        var width=image.width
        var height=image.height

        val bitmapRatio :Double=width.toDouble()/height.toDouble()

        if(bitmapRatio>1){
            width= maximumSize
            val scaledHeight =width/bitmapRatio
            height=scaledHeight.toInt()
        }else{
            height=maximumSize
            val scaledWidth=height*bitmapRatio
            width=scaledWidth.toInt()
        }
        return  Bitmap.createScaledBitmap(image,width,height,true)

    }



    //IZIN ALINDIGI ANDA SONUCA GORE YAPILACAK ISLEM
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //ALINAN IZINLE VERILEN IZINLERIN KODU ESLESIYOR MU?
        if(requestCode==1){
            //SONUCLARDA IZIN VAR MI?
            if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //Galeriye gitmek icin yapilan intent (URI -> Gorselin adresi)
                val intentGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intentGallery,2)
            }else{
                Toast.makeText(applicationContext,"Permission Denied!",Toast.LENGTH_LONG).show()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //RESIM SECILDI MI VE IZINLER ESLESIYOR MU VERI VAR MI
        if(requestCode==2 && resultCode== RESULT_OK && data!=null){
            //RESMIN HAFIZA YOLUNU KAYDETTIK
            selectedImage=data.data

            if(selectedImage!=null){
                if(Build.VERSION.SDK_INT>=28){
                    //SDK Version >=28 ISE YAPILMASI GEREKEN ISLEMLER
                    val source=ImageDecoder.createSource(this.contentResolver,selectedImage!!)
                    selectedBitmap=ImageDecoder.decodeBitmap(source)
                    imageView.setImageBitmap(selectedBitmap)
                }else{
                    //RESMIN BITMAP' ini ALIRKEN KULLANILAN FUNCTION TEDAVULDEN KALKMIS O YUZDEN SDK Version BAKMAMIZ GEREKLI
                    //YA DA DIGER YONTEMI KULLANACAGIZ ASAGIDA BIRINCI BITMAP YONTEMI
                    selectedBitmap=MediaStore.Images.Media.getBitmap(this.contentResolver,selectedImage)
                    imageView.setImageBitmap(selectedBitmap)
                }
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

}