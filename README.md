# AndroidStudio_Java_Project03_GameArchive
 Android Studio ile bir takım oyunların bilgilerini barındıran, basit kodlar içeren ve veritabanı kullanan bir proje.
 
 
 # Android Studio GameArchive Version 0.1
 
 Kendimi geliştirmek adına öğrendiğim kodları programlarımda kullanabilmek için ara ara yeni projeler açıp geliştirmeyi planladığımı dile getirmiştim.
 Yeni projemle birlikte veritabanı kullanımına ufak bir giriş yapmaya başladım.
 
 Programın Amacı:
 - Sevdiğim oyunların kısa bilgilerini içerisinde tutmak.
 - Veritabanını kullanmayı öğrenmek.
 - Gelecek versiyonlarda UPDATE, DELETE gibi komutları proje üzerinde göstermek.
 
 Kullanılan yeni bilgiler:
 - Kullanıcılardan verilerine erişebilmek için Gallery izni.
 - Manifest.permission' a  READ_EXTERNAL_STORAGE ekleme.
 - Intent ile Gallery yönlendirme (
 intent ile galery yönlendirme:
 Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery,2);
 )
 - İzinleri düzenleme: (
 onRequestPermissionsResult
 )
 - Application Menüsü oluşturma: (
 res->new -> new directory ("Menu")

res->menu->Right Click-> new -> menu resource file

menü xml code
<item android:id="@+id/add_game_item" android:title="Add Game"></item>
 )
 
 Gibi kolayca yazılabilen kodlar ile ufak bir çalışma. Gelecek versiyonlar ile daha ileriye götürmeye çalışacağım.
