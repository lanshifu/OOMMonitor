package com.lanshifu.oommonitor

import android.os.Bundle
import android.os.Process
import android.view.View
import android.widget.EditText

import android.widget.TextView
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.lang.StringBuilder
import java.lang.Exception


class MainActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("OOM测试")
        setContentView(R.layout.activity_main)
        dashboard = findViewById(R.id.tv_dashboard);
        tv_fd = findViewById(R.id.tv_fd);
        tv_memory = findViewById(R.id.tv_memory);
        tv_fd_max = findViewById(R.id.tv_fd_max);
        etDigtal=findViewById(R.id.et_digtal);
        findViewById<View>(R.id.bt1).setOnClickListener(this);
        findViewById<View>(R.id.bt2).setOnClickListener(this);
        findViewById<View>(R.id.bt3).setOnClickListener(this);
        findViewById<View>(R.id.bt4).setOnClickListener(this);
        findViewById<View>(R.id.bt5).setOnClickListener(this);
        findViewById<View>(R.id.bt6).setOnClickListener(this);
        findViewById<View>(R.id.bt7).setOnClickListener(this);
        findViewById<View>(R.id.bt8).setOnClickListener(this);

        val file = File(getExternalFilesDir("test")!!.absolutePath +"/test.txt")
        if (!file.exists()){
            file.createNewFile()
        }
        GlobalScope.launch {
            val randomAccessFile = RandomAccessFile("/proc/" + android.os.Process.myPid().toString() + "/limits", "r")
            val stringBuilder = StringBuilder()
            var s: String?
            while (randomAccessFile.readLine().also { s = it } != null) {
                s?.let {
                    if(it.contains("Max open files")){
                        stringBuilder.append(s).append("\r\n")
                    }
                }

            }


            withContext(Dispatchers.Main){
                tv_fd_max?.text = stringBuilder.toString()
            }

        }

        GlobalScope.launch {
            val fdFile = File("/proc/" + Process.myPid().toString() + "/fd")
            val files = fdFile.listFiles()

            val memory = StringBuilder()
            var maxMemory = Runtime.getRuntime().maxMemory() /1024 /1024
            var freeMemory = Runtime.getRuntime().freeMemory() /1024 /1024
            var totalMemory = Runtime.getRuntime().totalMemory() /1024 /1024
            memory.append("maxMemory:$maxMemory M\n")
            memory.append("totalMemory:$totalMemory M\n")
            memory.append("freeMemory:$freeMemory M\n")
            memory.append("used:${totalMemory - freeMemory}")

            withContext(Dispatchers.Main){
                if (files != null) {
                    tv_fd?.text = "当前fd数：" + files.size
                } else {
                    tv_fd?.text = "/proc/pid/fd is empty "
                }

                tv_memory?.text = memory.toString()
            }

        }


    }



    private val ERROR_HINT = "输入数字"
    val UNIT_M = (1024 * 1024).toFloat()
    private var dashboard: TextView? = null
    private var tv_fd: TextView? = null
    private var tv_memory: TextView? = null
    private var tv_fd_max: TextView? = null
    private var etDigtal: EditText? = null
    private var digtal = -1
    private var heap = mutableListOf<ByteArray>()
    private var fdList = mutableListOf<BufferedReader>()

    private val emptyRunnable = Runnable {
        try {
            Thread.sleep(Long.MAX_VALUE)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun onClick(view: View) {
        digtal = try {
            Integer.valueOf(etDigtal!!.text.toString())
        } catch (e: Exception) {
            -1
        }
        when (view.id) {
            R.id.bt1 -> showFileContent("/proc/" + android.os.Process.myPid().toString() + "/limits")
            R.id.bt2 -> if (digtal <= 0) {
                dashboard!!.text = ERROR_HINT
            } else {
                var i = 0

                while (i < digtal) {

                    var bufferedReader = BufferedReader(
                        FileReader(
                            getExternalFilesDir("test")!!.absolutePath +"/test.txt"
                        )
                    )
                    fdList.add(bufferedReader)
                    i++
                }
            }
            R.id.bt3 -> {
                val fdFile = File("/proc/" + Process.myPid().toString() + "/fd")
                val files = fdFile.listFiles()
                if (files != null) {
                    dashboard!!.text = "current FD numbler is " + files.size
                } else {
                    dashboard!!.text = "/proc/pid/fd is empty "
                }
            }
            R.id.bt4 -> showFileContent("/proc/" + android.os.Process.myPid().toString() + "/status")
            R.id.bt5 -> if (digtal <= 0) {
                dashboard!!.text = ERROR_HINT
            } else {
                var i = 0
                while (i < digtal) {
                    Thread(emptyRunnable).start()
                    i++
                }
            }
            R.id.bt6 -> {
                val stringBuilder = StringBuilder()
                stringBuilder.append("Java Heap Max : ")
                    .append(Runtime.getRuntime().maxMemory() / UNIT_M).append(" MB\r\n")
                stringBuilder.append("Current used  : ").append(
                    (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
                        .freeMemory()) / UNIT_M
                ).append(" MB\r\n")
                dashboard!!.text = stringBuilder.toString()
            }
            R.id.bt7 -> if (digtal <= 0) {
                dashboard!!.text = ERROR_HINT
            } else {
                val bytes = ByteArray(digtal)
                heap.add(bytes)
            }
            R.id.bt8 -> {
                heap = ArrayList()
                System.gc()
            }
        }
    }

    private fun showFileContent(path: String) {
        if (TextUtils.isEmpty(path)) {
            return
        }
        try {
            val randomAccessFile = RandomAccessFile(path, "r")
            val stringBuilder = StringBuilder()
            var s: String?
            while (randomAccessFile.readLine().also { s = it } != null) {
                s?.let {
                    if(it.contains("Limit") || it.contains("files")  || it.contains("thread") ){
                    }
                    stringBuilder.append(s).append("\r\n")
                }

            }
            dashboard!!.text = stringBuilder.toString()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}