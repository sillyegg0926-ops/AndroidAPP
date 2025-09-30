package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.Calculator

class MainActivity3 : AppCompatActivity() {





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main3)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
val calculator = Calculator()
        val button1 = findViewById<Button>(R.id.button1)
        button1.setOnClickListener {
            calculator.inputNumber("7")
        }
        val button2 = findViewById<Button>(R.id.button2)
        button2.setOnClickListener {
            calculator.inputNumber("8")
        }
        val button3 = findViewById<Button>(R.id.button3)
        button3.setOnClickListener {
            calculator.inputNumber("9")
        }
//        val button4 = findViewById<Button>(R.id.button4)
//        button4.setOnClickListener {
//            calculator.remove("/")
//        }
        val button5 = findViewById<Button>(R.id.button5)
        button5.setOnClickListener {
            calculator.inputNumber("4")
        }
        val button6 = findViewById<Button>(R.id.button6)
        button6.setOnClickListener {
            calculator.inputNumber("5")
        }
        val button7 = findViewById<Button>(R.id.button7)
        button7.setOnClickListener {
            calculator.inputNumber("6")
        }
//        val button8 = findViewById<Button>(R.id.button8)
//        button8.setOnClickListener {
//            calculator.take("X")
//        }
        val button9 = findViewById<Button>(R.id.button9)
        button9.setOnClickListener {
            calculator.inputNumber("1")
        }
        val button0 = findViewById<Button>(R.id.button10)
        button0.setOnClickListener {
            calculator.inputNumber("2")
        }
        val buttonAdd = findViewById<Button>(R.id.button11)
        buttonAdd.setOnClickListener {
            calculator.inputOperator("3")
        }
//        val buttonSubtract = findViewById<Button>(R.id.button12)
//        buttonSubtract.setOnClickListener {
//            calculator.reduce()
//        }
        val buttonMultiply = findViewById<Button>(R.id.button13)
        buttonMultiply.setOnClickListener {
            calculator.inputOperator("0")
        }
        val buttonDivide = findViewById<Button>(R.id.button14)
        buttonDivide.setOnClickListener {
            calculator.clear()
        }
//        val buttonEqual = findViewById<Button>(R.id.button15)
//        buttonEqual.setOnClickListener {
//            calculator.equal()
//        }
//        val buttonClear = findViewById<Button>(R.id.button16)
//        buttonClear.setOnClickListener {
//            calculator.add()
//        }







}
}