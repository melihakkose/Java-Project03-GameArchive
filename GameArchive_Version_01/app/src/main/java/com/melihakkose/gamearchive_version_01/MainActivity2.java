package com.melihakkose.gamearchive_version_01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity2 extends AppCompatActivity {
    Bitmap selectedImage;
    ImageView imageView;
    EditText txtbox_gamename,txtbox_releasedate,txtbox_developer,txtbox_platforms;
    Button button_save;
    SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        imageView=findViewById(R.id.imageView);
        txtbox_developer=findViewById(R.id.txtbox_developer);
        txtbox_gamename=findViewById(R.id.txtbox_gamename);
        txtbox_platforms=findViewById(R.id.txtbox_platforms);
        txtbox_releasedate=findViewById(R.id.txtbox_releasedate);
        button_save=findViewById(R.id.button_save);
        database=this.openOrCreateDatabase("Games",MODE_PRIVATE,null);

        Intent intent=getIntent();
        String info=intent.getStringExtra("activityNumb");

        if(info.matches("second")){

            txtbox_gamename.setText("");
            txtbox_releasedate.setText("");
            txtbox_platforms.setText("");
            txtbox_developer.setText("");
            button_save.setVisibility(View.VISIBLE);

            Bitmap selectImage= BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.selectimage);
            imageView.setImageBitmap(selectImage);
            txtbox_gamename.setEnabled(true);
            txtbox_releasedate.setEnabled(true);
            txtbox_platforms.setEnabled(true);
            txtbox_developer.setEnabled(true);
            imageView.setEnabled(true);

        }else{
            int gameID=intent.getIntExtra("id",1);
            button_save.setVisibility(View.INVISIBLE);
            txtbox_gamename.setEnabled(false);
            txtbox_releasedate.setEnabled(false);
            txtbox_platforms.setEnabled(false);
            txtbox_developer.setEnabled(false);
            imageView.setEnabled(false);

            try {
                //gameName VARCHAR,releaseDate VARCHAR,developer VARCHAR,platforms VARCHAR,image BLOB
                Cursor cursor =database.rawQuery("SELECT * FROM games WHERE id=?",new String[]{String.valueOf(gameID)});
                int gameIx =cursor.getColumnIndex("gameName");
                int releasedateIx =cursor.getColumnIndex("releaseDate");
                int developerIx =cursor.getColumnIndex("developer");
                int platformIx=cursor.getColumnIndex("platforms");
                int imageIx=cursor.getColumnIndex("image");
                while (cursor.moveToNext()){
                    txtbox_gamename.setText(cursor.getString(gameIx));
                    txtbox_developer.setText(cursor.getString(developerIx));
                    txtbox_platforms.setText(cursor.getString(platformIx));
                    txtbox_releasedate.setText(cursor.getString(releasedateIx));

                    byte[] bytes =cursor.getBlob(imageIx);
                    Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    imageView.setImageBitmap(bitmap);
                }
                cursor.close();


            }catch (Exception e){

            }

        }


    }

    public void selectimage (View view){

        /*Gallery ulasma izin kontrolu  Context/Kontrol edilecek izin
        PERMISSION_GRANTED or PERMISSION_DENIED
        Izin yok ise asagidaki islem yapilir */

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            /* Istenilen dizi izinler dizisi. Biz bir izin isteyecegimiz icin elemani belirttik.
            requestCode kullanilan izne yonelik yazilacak kodlar icin gerekli!! */

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);


            //Izin verilir ise asagidaki islem yapilir. Gallery yonlendirilir.
        }else{
            Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery,2);

        }

    }

    /*
    * Izin verildiginde ne olacagina dair uygulanan kod.
    * requestCode atamalari burada kullanilacak.
    * grantResults bize geri donen degerleri barindirir.
    * Izin verildigi takdirde kullaniciyi gallery' e yonlendirecegiz.
    * */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery,2);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //Secilen verierin bize geri donmesi ıcın bir method kullanmaliyiz
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Resim secili, veri null degil ise
        if(requestCode==2 && resultCode==RESULT_OK && data!=null){

            Uri imageData= data.getData();
            try {
                if(Build.VERSION.SDK_INT>=28){
                    ImageDecoder.Source source= ImageDecoder.createSource(this.getContentResolver(),imageData);
                    selectedImage =ImageDecoder.decodeBitmap(source);
                    imageView.setImageBitmap(selectedImage);
                }else{
                    selectedImage =MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageData);
                    imageView.setImageBitmap(selectedImage);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //Veritabani kayit methodu
    public void save (View view){
        //Veri tanimlamalari
        String gameName=txtbox_gamename.getText().toString();
        String releaseDate=txtbox_releasedate.getText().toString();
        String developerName=txtbox_developer.getText().toString();
        String platformName=txtbox_platforms.getText().toString();

        Bitmap smallImage=makeSmallerImage(selectedImage,300);

        //Image icin ayri veri tipi olusturma ve SQLite cokmemesi icin veri boyutu kucultme islemi
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray= outputStream.toByteArray();

        //Verileri kullanicidan alacagimiz icin SQLiteStatement kullandik.
        try {

            database = this.openOrCreateDatabase("Games",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS games " +
                    "(id INTEGER PRIMARY KEY,gameName VARCHAR,releaseDate VARCHAR,developer VARCHAR,platforms VARCHAR,image BLOB)");

            String sqlString="INSERT INTO games (gameName,releaseDate,developer,platforms,image) VALUES(?,?,?,?,?)";
            SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,gameName);
            sqLiteStatement.bindString(2,releaseDate);
            sqLiteStatement.bindString(3,developerName);
            sqLiteStatement.bindString(4,platformName);
            sqLiteStatement.bindBlob(5,byteArray);

            sqLiteStatement.execute();



        }catch (Exception e){

        }
        //finish();

        Intent intent =new Intent(MainActivity2.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);



    }

    //Image icin boyut konrol methodu
    public  Bitmap makeSmallerImage(Bitmap image, int maximumSize){

        int width =image.getWidth(); // genislik
        int height=image.getHeight(); //yukseklik

        float bitmapRatio=(float) width/(float) height;

        //Yatay mi dikey mi kontrolu
        if(bitmapRatio >1){
            width=maximumSize;
            height=(int) (width/bitmapRatio);
        }else{
            height=maximumSize;
            width=(int)(height*bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image,width,height,true);

    }

}