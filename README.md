## UAS_Pemogaramn_Mobile

### Anggota Kelompok <br>

| Nama                      | NIM       | Kelas     | Mata Kuliah          |
| ------------------------- | --------- | --------- | -------------------- |
| Frans Putra Sinaga        | 312210046 | TI.22.A.1 | Pemrograman Mobile 2 |
| Fathia Wardah S.Djawas    | 312210197 | TI.22.A.1 | Pemrograman Mobile 2 |
| Aas Novitasari            | 312210167 | TI.22.A.1 | Pemrograman Mobile 2 |

Tujuan Aplikasi:

Aplikasi menggunakan API spreadsheet memungkinkan interaksi otomatis dengan data spreadsheet tanpa perlu membuka file langsung. Ini memungkinkan pengambilan, pengeditan, dan penyimpanan data secara real-time, mendukung kolaborasi, dan menyediakan keamanan melalui otentikasi. Dengan API ini, aplikasi dapat memberikan fitur manajemen dan analisis data yang lebih efisien.

Fitur-fitur Aplikasi:
- Bayar Iuran
- Laporan
- Form Warga
- Data Warga
- Laporan Warga
- Maps


Langkah-langkah Praktikum:
- Registrasi dan Dapatkan API Key: Daftar dan peroleh API key dari ApiSpreadsheets.
- Identifikasi Spreadsheet: Tentukan ID spreadsheet yang berisi data keuangan KAS RT yang akan diakses oleh aplikasi Anda.
- Buat Proyek Android: Buat proyek baru dalam lingkungan pengembangan Android Studio.
- Desain Antarmuka Pengguna: Rencanakan dan buat desain antarmuka pengguna (UI) yang sesuai dengan rincian yang diberikan.
- Koneksi ke API: Buat logika koneksi ke API menggunakan Retrofit atau pustaka HTTP klien lainnya dalam aplikasi Android Anda.
- Parsing Data JSON: Implementasikan logika parsing JSON untuk mengambil data dari respons API.
- Tampilkan Data: Tampilkan data yang diperoleh dari API ke dalam antarmuka pengguna aplikasi Android sesuai dengan rincian yang diminta.
- Uji Aplikasi: Lakukan pengujian menyeluruh terhadap aplikasi Anda untuk memastikan bahwa semua fitur berjalan dengan baik dan data ditampilkan dengan benar.

 Build Gradle
```
  dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    implementation ("com.github.bumptech.glide:glide:4.16.0")
```

Dokumentasi Tambahan:
1. YouTube : https://youtu.be/ECawWFLfcnU?si=Sv_besYfIj_6s7D3
2. Link Pdf : https://drive.google.com/file/d/1gO2bnvH5MBat6D-6y2cr6ZLITKr8hoqT/view?usp=sharing

# Terima Kasih
