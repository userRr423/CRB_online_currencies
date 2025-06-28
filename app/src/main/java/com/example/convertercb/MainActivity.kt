package com.example.convertercb

import android.content.res.Resources.Theme
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.w3c.dom.Document
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    var allParseText:String = ""
    var CharCodeName = mutableListOf<String>()
    var CharCodeNameOfValute = mutableListOf<String>()
    var CharCodeNameOfValueValute = mutableListOf<String>()

    var numberOfValute = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //System.out.println("dwdwd")

        val url = "https://www.cbr.ru/scripts/XML_daily.asp"

        val tvText = findViewById<TextView>(R.id.textView)
        val tvText2 = findViewById<TextView>(R.id.textView2)
        val tvout = findViewById<TextView>(R.id.outText)
        val button = findViewById<Button>(R.id.button)
        val editText = findViewById<EditText>(R.id.inputMoneyCount);

        button.setBackgroundColor(Color.GREEN);

        var valuta1:String = ""
        var valuta2:String = ""

        parseWebsite(url) { result ->
            runOnUiThread {
                // Обработка результата на основном потоке
                allParseText = result

                val input = allParseText
                val regex = "<CharCode>([^<]+)</CharCode>".toRegex()

                regex.findAll(input).forEach { result ->
                    //allParseText += result.groupValues[1]
                    CharCodeName.add(result.groupValues[1])
                }

                val inputV = allParseText
                val regexV = "<Value>([^<]+)</Value>".toRegex()

                regexV.findAll(inputV).forEach { result ->
                    //allParseText += result.groupValues[1]
                    CharCodeNameOfValueValute.add(result.groupValues[1])
                }

                //System.out.println(CharCodeNameOfValueValute)

                val regex2 = Regex("<Name>([^<]+)</Name>")
                val matchResults = regex2.findAll(allParseText)


                var s = mutableSetOf<String>()
                for (matchResult in matchResults) {
                    val nameValue = matchResult.groups[1]?.value
                    //println(nameValue)
                    CharCodeNameOfValute.add(nameValue.toString())
                }


                var sizel = CharCodeNameOfValute.size / 3
                //Log.d("MyTag", CharCodeNameOfValute.subList(0, 43).toString()) //42
                //tvText.setText(CharCodeName.toString().trim())


                CharCodeNameOfValute = CharCodeNameOfValute.subList(0, 43).toMutableList()
                Log.d("MyTag", CharCodeNameOfValute.toString())
                CharCodeNameOfValueValute = CharCodeNameOfValueValute.subList(0, 43).toMutableList()


                val spinner:Spinner = findViewById(R.id.spinner)

                val items = CharCodeNameOfValute // Данные для списка

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter


                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedItem = items[position] // Получение выбранного элемента из списка
                        tvText.setText(CharCodeName[position])
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Обработка события, если ничего не выбрано
                    }
                }




                val spinner2:Spinner = findViewById(R.id.spinner2)

                val items2 = CharCodeNameOfValute // Данные для списка

                val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, items2)
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner2.adapter = adapter2


                spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedItem = items2[position] // Получение выбранного элемента из списка
                        tvText2.setText(CharCodeName[position])
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Обработка события, если ничего не выбрано
                    }
                }


                button.setOnClickListener {
                    valuta1 = CharCodeNameOfValueValute[spinner.selectedItemPosition].trim()
                    valuta2 = CharCodeNameOfValueValute[spinner2.selectedItemPosition].trim()



                    val v1 = delSymbol(valuta1)
                    val v2 = delSymbol(valuta2)

                    //System.out.println("$v1  $v2")

                    var edstr = editText.text.toString()

                    if (isFloat(edstr))
                        numberOfValute = edstr.toFloat()

                    if(numberOfValute != 0.0f) {
                        val res1 = numberOfValute * v1.toFloat()
                        val res2 = res1 / v2.toFloat()

                        tvout.setText(res2.toString())
                    }
                }

            }


        }

    }

    fun parseWebsite(url: String, callback: (String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val doc = Jsoup.connect(url).get()
                val title: String = doc.allElements.toString()

                // Вызов колбэка с результатом парсинга
                callback(title)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun isFloat(input: String): Boolean {
        return try {
            input.toFloat()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun delSymbol(valuta:String): String {
        val inputString = valuta
        var resultString = ""

        for (char in inputString) {
            val newChar = if (char == ',') '.' else char
            resultString += newChar
        }
        return resultString
    }


}