package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.Calculator





class MainActivity3 : AppCompatActivity() {


    private lateinit var calculator: Calculator
    private lateinit var displayText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main3)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        calculator = Calculator()
        displayText = findViewById(R.id.text)

        val numberButtons = listOf(
            R.id.button1, R.id.button2, R.id.button3,
            R.id.button5, R.id.button6, R.id.button7,
            R.id.button8, R.id.button9, R.id.button10,
            R.id.button11, R.id.button13
        )
        for (id in numberButtons) {
            val button = findViewById<Button>(id)
            button.setOnClickListener {
                calculator.inputNumber(button.text.toString())
                updateDisplay()
            }

        }
        findViewById<Button>(R.id.button16).setOnClickListener {onOperatorClick("+") }
        findViewById<Button>(R.id.button12).setOnClickListener {onOperatorClick("-") }
        findViewById<Button>(R.id.button8).setOnClickListener {onOperatorClick("×") }
        findViewById<Button>(R.id.button4).setOnClickListener {onOperatorClick("÷") }

        findViewById<Button>(R.id.button15).setOnClickListener {
            calculator.calculate()
            updateDisplay()
        }


        findViewById<Button>(R.id.button14).setOnClickListener {
            calculator.clear()
            updateDisplay()
        }
        updateDisplay()

    }
    private fun updateDisplay() {
        displayText.text = calculator.display
    }
    private fun onOperatorClick(op: String) {
        calculator.inputOperator(op)
        updateDisplay()
    }
}











