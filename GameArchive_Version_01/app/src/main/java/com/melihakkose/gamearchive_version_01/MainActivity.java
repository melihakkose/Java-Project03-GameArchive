package com.melihakkose.gamearchive_version_01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;

    //Oyun adi ve id sini tutacagimiz listler.
    ArrayList<String> gameArray;
    ArrayList<Integer> idArray;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //list veiw ve listlerin tanimi
        listView=findViewById(R.id.listView);
        gameArray=new ArrayList<String>();
        idArray=new ArrayList<Integer>();

        //ArrayAdapter
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,gameArray);
        listView.setAdapter(arrayAdapter); //listview' de gozukmesi icin gerekli

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(MainActivity.this,MainActivity2.class);
                intent.putExtra("id",idArray.get(i));
                intent.putExtra("activityNumb","first");
                startActivity(intent);
            }
        });

        //asagida yazdigimiz veri alma fonksiyonu
        getData();
    }

    //Veri cekme fonksşyonu
    public void getData(){
        try {
            SQLiteDatabase database=this.openOrCreateDatabase("Games",MODE_PRIVATE,null);

            Cursor cursor=database.rawQuery("SELECT * FROM games",null); //verileri secmek icin cursor

            int gameIx=cursor.getColumnIndex("gameName");
            int idIx=cursor.getColumnIndex("id");

            while (cursor.moveToNext()){
                gameArray.add(cursor.getString(gameIx));
                idArray.add(cursor.getInt(idIx));

            }
            arrayAdapter.notifyDataSetChanged(); //listede degisiklik olunca gostermek icin gerekli
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Menu override fonksiyonu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //INFLATE.  XML olarak olusturdugumuz menuyu programimiza tanitmamiz gerekli.

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.add_game,menu);

        return super.onCreateOptionsMenu(menu);
    }
    //Menu override secilen item icin gerekli fonk
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        /*MENU' den islem secildigi zaman yapilacak islem. Yeni Activity baslatmak olacak.
        * Ayni zamanda bu menun her item icin bir id' si olmali onlari da almaliyiz.*/


        if(item.getItemId()==R.id.add_game_item){
            Intent intent=new Intent(MainActivity.this,MainActivity2.class);
            intent.putExtra("activityNumb","second");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}