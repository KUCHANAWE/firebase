package com.example.firebase

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    var DataProvinsi = ArrayList<daftarProvinsi>()
    //        lateinit var lvAdapter: ArrayAdapter<daftarProvinsi>
    lateinit var lvAdapter : SimpleAdapter
    lateinit var _etProvinsi: EditText
    lateinit var _etIbuKota: EditText
    var data: MutableList<Map<String, String>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        _etProvinsi = findViewById<EditText>(R.id.etProvinsi)
        _etIbuKota = findViewById<EditText>(R.id.etIbuKota)
        val _btnSimpan = findViewById<Button>(R.id.btnSimpan)
        val _lvData = findViewById<ListView>(R.id.lvData)

        val db = Firebase.firestore

        readData(db)

        lvAdapter = SimpleAdapter(
            this,
            data,
            android.R.layout.simple_list_item_2,
            arrayOf<String>("Pro","Ibu"),
            intArrayOf(
                android.R.id.text1,
                android.R.id.text2
            )
        )

//        lvAdapter = ArrayAdapter(
//            this,
//            android.R.layout.simple_list_item_1,
//            DataProvinsi
//        )

        _lvData.adapter = lvAdapter

        _btnSimpan.setOnClickListener{
            TambahData(db, _etProvinsi.text.toString(), _etIbuKota.text.toString())
            readData(db)
        }

        _lvData.setOnItemClickListener{parent, view, posisition, id ->
            val namaPro = data[posisition].get("Pro")
            if (namaPro != null){
                db.collection("tbProvinsi")
                    .document(namaPro)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("Firebase", "Berhasil diHAPUS")
                        readData(db)
                    }
                    .addOnFailureListener{ e ->
                        Log.w("Firebase", e.message.toString())
                    }
            }
            true
        }
    }

    fun TambahData(db: FirebaseFirestore, Provinsi: String, Ibukota: String) {
        val dataBaru = daftarProvinsi(Provinsi, Ibukota)
        db.collection("tbProvinsi")
            .document(dataBaru.provinsi)
            .set(dataBaru)
            .addOnSuccessListener{
                _etProvinsi.setText("")
                _etIbuKota.setText("")
                Log.d("Firebase","Data Berhasil Disimpan")
                readData(db)
            }
            .addOnFailureListener{
                Log.d("Firebase",it.message.toString())
            }
    }

    fun readData(db:FirebaseFirestore){
        db.collection("tbProvinsi").get()
            .addOnSuccessListener {
                    result ->
                DataProvinsi.clear()
                for (document in result){
                    val readData = daftarProvinsi(
                        document.data.get("provinsi").toString(),
                        document.data.get("ibukota").toString()
                    )
                    DataProvinsi.add(readData)
                    data.clear()
                    DataProvinsi.forEach{
                        val dt: MutableMap<String, String> = HashMap(2)
                        dt["Pro"] = it.provinsi
                        dt["Ibu"] = it.ibukota
                        data.add(dt)
                    }
                }
                lvAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener{
                Log.d("Firebase", it.message.toString())
            }

    }
}

