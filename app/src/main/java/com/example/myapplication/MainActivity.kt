package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat




class MainActivity : AppCompatActivity() {
    val username = "123"
    val password = "123"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editText1 = findViewById<EditText>(R.id.edittext1)
        val editText2 = findViewById<EditText>(R.id.edittext2)

        val button1 = findViewById<Button>(R.id.button1)
        button1.setOnClickListener {


            if (editText1.text.toString() == username && editText2.text.toString() == password) {
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)

            }else {
                    Toast.makeText(this, "請輸入正確使用者名稱和密碼", Toast.LENGTH_LONG).show()
                }

            }


            val button2 = findViewById<Button>(R.id.button2)
            button2.setOnClickListener {

                if (editText1.text.toString().isEmpty() || editText2.text.toString().isEmpty()) {
                    Toast.makeText(this, "請輸入使用者名稱和密碼", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                } else {
                    Toast.makeText(this, "註冊成功", Toast.LENGTH_LONG).show()

                }

            }

        }
    }

