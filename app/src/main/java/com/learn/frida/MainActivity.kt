package com.learn.frida

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import java.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    init {
        System.loadLibrary("frida")
    }

    external fun isFridaRunning(): Boolean;
    external fun isFridaProc(): Boolean;

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("com.learn.frida ~ learnfrida.info")
        setContentView(R.layout.activity_main)


        // Ensure the value is set before the button can be clicked
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(getString(R.string.savedString), "foobarstring")
            apply()
        }

        val btn_sharedprefs = findViewById(R.id.sharedPrefsButton) as Button
        btn_sharedprefs.setOnClickListener {
            val sharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
            val stringValue =
                sharedPreferences.getString(R.string.savedString.toString(), "foobarstring")

            if (stringValue == "foobarstring") {
                Toast.makeText(this, "Instrumentation is not correct.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Instrumentation OK!", Toast.LENGTH_SHORT).show()
            }
        }

        val btnHttpStore = findViewById<Button>(R.id.httpStoreBtn) as Button
        btnHttpStore.setOnClickListener {
            thread {
                val url = URL("https://postman-echo.com/post")
                val postData = "foo1=bar1&foo2=bar2"

                val conn = url.openConnection()
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                conn.setRequestProperty("Content-Length", postData.length.toString())

                DataOutputStream(conn.getOutputStream()).use { it.writeBytes(postData) }
                BufferedReader(InputStreamReader(conn.getInputStream())).use { bf ->
                    var line: String?
                    while (bf.readLine().also { line = it } != null) {
                        println(line)
                    }
                }
            }
        }

        val btnB64Decode = findViewById<Button>(R.id.b64decodeBtn) as Button;
        btnB64Decode.setOnClickListener {
            val encodedString =
                "SSBob3BlIHlvdSBhcmUgaGF2aW5nIGZ1biB3aXRoIEZyaWRhIQ==".toByteArray();
            val b64decoder = Base64.getDecoder();
            val decoded_string = String(b64decoder.decode(encodedString));
            Toast.makeText(this, "Called Base64!", Toast.LENGTH_SHORT).show();
        }

        val frida_button = findViewById(R.id.checkFridaServerBtn) as Button
        frida_button.setOnClickListener {
            if (isFridaRunning()) {
                frida_button.setBackgroundColor(Color.RED)
                Toast.makeText(this@MainActivity, "Frida is running", Toast.LENGTH_SHORT).show();
            } else {
                frida_button.setBackgroundColor(Color.BLUE)
                Toast.makeText(this@MainActivity, "Frida isnt running", Toast.LENGTH_SHORT).show();
            }

            try {
                throw Exception("foobar")
            } catch (e: Exception) {
                e.stackTrace.forEach {
                    Log.d("TAG", it.className)
                    Log.d("TAG", it.methodName)
                }
            }
        }
        val frida_proc_button = findViewById(R.id.checkProcBtn) as Button
        frida_proc_button.setOnClickListener {
            if (isFridaProc()) {
                Toast.makeText(this@MainActivity, "/proc Frida detected", Toast.LENGTH_SHORT).show()
                frida_proc_button.setBackgroundColor(Color.RED)
            } else {
                Toast.makeText(this@MainActivity, "/proc map not detected", Toast.LENGTH_SHORT)
                    .show()
                frida_proc_button.setBackgroundColor(Color.BLUE)
            }
        }
    }
}