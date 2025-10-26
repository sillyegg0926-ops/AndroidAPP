package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.MyAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.Locale


class MainActivity2 : AppCompatActivity() , MyAdapter.OnItemClickListener {

    private lateinit var profileImageView: ShapeableImageView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: MyAdapter


    private  var currencyDataClass = mutableListOf<CurrencyDataClas>()




    // 常量定義，在 Kotlin 中通常放在 companion object 內
    companion object {
        val client = OkHttpClient()

        // 2. 建立 Request 物件
        val request = Request.Builder()
            .url("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/twd.json") // 替換為您的 API 網址
            .build()

        const val PERMISSION_REQUEST_CODE = 100
        const val PREFS_NAME = "MyAppPreferences"
        const val KEY_PROFILE_IMAGE_PATH = "profile_image_path"
        const val PROFILE_IMAGE_FILENAME = "profile_image.jpg"
        const val IMAGE_MAX_SIZE = 800 // 最大寬度或高度
        const val IMAGE_QUALITY = 85 // JPEG 壓縮品質 (0-100)
    }

    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val selectedImageUri: Uri? = data?.data
                if (selectedImageUri != null) {
                    // 複製並壓縮圖片到內部儲存空間
                    if (saveImageToInternalStorage(selectedImageUri)) {
                        // 載入已保存的圖片
                        loadSavedImage()
                        showSnackbar("成功選擇圖片！", Snackbar.LENGTH_LONG)
                    } else {
                        showSnackbar( "儲存圖片失敗，請重試。", Snackbar.LENGTH_LONG)
                    }
                } else {
                    showSnackbar( "無法取得圖片。", Snackbar.LENGTH_LONG)
                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                showSnackbar("取消選擇圖片。", Snackbar.LENGTH_LONG)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBackPressedHandler()
        getCurrencyData()



        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        profileImageView = findViewById(R.id.ShapeableImageView)
        profileImageView.setOnClickListener {
            checkAndRequestStoragePermission()
        }
        loadSavedImage()
        // 1. 準備固定的資料
        recyclerView = findViewById(R.id.my_recycler_view)


        // 2. 找到 RecyclerView
       recyclerView.layoutManager = LinearLayoutManager(this)



        // 3. 設定 LayoutManager
        adapter = MyAdapter(currencyDataClass,this)

        // 4. 設定 Adapter
        recyclerView.adapter =adapter

        val buttonCal = findViewById<Button>(R.id.buttonCal)
        buttonCal.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }


    }

    override fun onItemClick(position: Int, item: CurrencyDataClas) {


        showSnackbar("詳細匯率:${item.currencyCodeText} ${item.currencyNameText} ${item.currencyRate} ", Snackbar.LENGTH_LONG)
//        Toast.makeText(this,"點擊了: $item (位置: $position)", Toast.LENGTH_SHORT).show()
    }

    override fun onItemLongClick(position: Int, item: CurrencyDataClas) {
        MaterialAlertDialogBuilder(this)
            .setTitle("選擇操作")
            .setItems(arrayOf("查看詳情", "複製匯率")) { _, which ->
                when (which) {
                    0 -> showCurrencyDataClasils(item)
                    1 -> copyReteToClipboard(item)
                }
            }
            .show()
    }


    private fun showCurrencyDataClasils(item: CurrencyDataClas,) {

        var reverseRate = 1.0 /item.currencyRate
        reverseRate = String.format(Locale.getDefault(),"%.2f", reverseRate).toDouble()

        MaterialAlertDialogBuilder(this)

            .setTitle("詳細匯率")
            .setMessage("貨幣代碼: ${item.currencyCodeText}\n貨幣名稱: ${item.currencyNameText}\n現在匯率: ${item.currencyRate} /${reverseRate}")
            .setPositiveButton("確定"){dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun copyReteToClipboard(itm: CurrencyDataClas) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("匯率", "${itm.currencyCodeText} ${itm.currencyRate}")
        clipboard.setPrimaryClip(clip)
        showSnackbar("已複製匯率到剪貼簿", Snackbar.LENGTH_LONG)



    }


    override fun editItem(position: Int, item: String) {
        Toast.makeText(this, "編輯: $item", Toast.LENGTH_SHORT).show()
    }

    override fun deleteItem(position: Int, item: String) {
        Toast.makeText(this, "刪除: $item", Toast.LENGTH_SHORT).show()
    }

    /**
     * 檢查並請求讀取外部儲存空間的權限。
     */
    private fun checkAndRequestStoragePermission() {
        // 對於 Android 14 (API 34) 及以上，使用 READ_MEDIA_VISUAL_USER_SELECTED 權限
        // 對於較舊版本，使用 READ_EXTERNAL_STORAGE 權限
        val permission = if (android.os.Build.VERSION.SDK_INT >= 34) {
            "android.permission.READ_MEDIA_VISUAL_USER_SELECTED"
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 權限尚未被授予，請求權限
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                PERMISSION_REQUEST_CODE
            )
        } else {
            // 權限已被授予，直接開啟圖庫
            openGallery()
        }

    }

    /**
     * 處理權限請求的結果。
     * 當使用者在權限對話框中做出選擇後，系統會呼叫此方法。
     */

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // 檢查 grantResults 是否不為空且第一個結果是授予
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 權限被授予
                Toast.makeText(this, "圖片權限已獲取，可以選擇圖片。", Toast.LENGTH_SHORT).show()
                openGallery()
            } else {
                // 權限被拒絕
                showPermissionDeniedDialog()
            }
        }
    }

    /**
     * 顯示權限被拒絕的對話框，提供使用者選項
     */
    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("需要圖片權限")
            .setMessage("為了選擇圖片，應用程式需要存取您的圖片權限。請前往設定頁面開啟權限，或選擇「不再詢問」來取消操作。")
            .setPositiveButton("前往設定") { _, _ ->
                // 開啟應用程式設定頁面
                openAppSettings()
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "已取消選擇圖片", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }

    /**
     * 開啟應用程式的設定頁面
     */
    private fun openAppSettings() {
        val intent =
            android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = android.net.Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    /**
     * 開啟系統圖庫選擇圖片。
     */
    private fun openGallery() {
        // 嘗試多種方式開啟圖片選擇器
        val intents = mutableListOf<Intent>()

        // 方式1: 使用 ACTION_PICK (推薦)
        val pickIntent = Intent(Intent.ACTION_PICK).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        }
        intents.add(pickIntent)

        // 方式2: 使用 ACTION_GET_CONTENT (備用)
        val getContentIntent = Intent(Intent.ACTION_GET_CONTENT)
        getContentIntent.type = "image/*"
        intents.add(getContentIntent)

        // 尋找第一個可用的 Intent
        var selectedIntent: Intent? = null
        for (intent in intents) {
            if (intent.resolveActivity(packageManager) != null) {
                selectedIntent = intent
                break
            }
        }

        if (selectedIntent != null) {
            // 使用找到的 Intent 啟動圖片選擇器
            pickImageLauncher.launch(selectedIntent)
        } else {
            // 如果所有方式都失敗，顯示錯誤訊息並提供替代方案
            showNoGalleryAppDialog()
        }


        /**
         * 當沒有找到圖片選擇器應用程式時顯示對話框
         */
        }
        private fun showNoGalleryAppDialog() {
            AlertDialog.Builder(this)
                .setTitle("無法選擇圖片")
                .setMessage("您的裝置上沒有找到圖片選擇器應用程式。請安裝一個圖庫應用程式（如 Google Photos）或檔案管理器，然後重試。")
                .setPositiveButton("前往 Play 商店") { _, _ ->
                    openPlayStore()
                }
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
        }

        /**
         * 開啟 Play 商店搜尋圖庫應用程式
         */
        private fun openPlayStore() {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = "market://search?q=gallery+photo+manager".toUri()
                startActivity(intent)
            } catch (e: Exception) {
                // 如果 Play 商店應用程式不可用，使用網頁版
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = "https://play.google.com/store/search?q=gallery%20photo%20manager".toUri()
                startActivity(intent)
            }
        }
    /**
     * 將圖片保存到內部儲存空間（包含壓縮）
     */
    private fun saveImageToInternalStorage(uri: Uri): Boolean {
        return try {
            // 讀取原始圖片
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) {
                return false
            }

            // 壓縮圖片
            val compressedBitmap = compressImage(originalBitmap)

            // 保存到內部儲存空間
            val file = File(filesDir, PROFILE_IMAGE_FILENAME)
            FileOutputStream(file).use { outputStream ->
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream)
            }

            // 保存文件路徑到 SharedPreferences
            sharedPreferences.edit().apply {
                putString(KEY_PROFILE_IMAGE_PATH, file.absolutePath)
                apply()
            }

            // 釋放 Bitmap 資源
            if (originalBitmap != compressedBitmap) {
                originalBitmap.recycle()
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    /**
     * 壓縮圖片到指定大小
     */
    private fun compressImage(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // 如果圖片已經夠小，直接返回
        if (width <= IMAGE_MAX_SIZE && height <= IMAGE_MAX_SIZE) {
            return bitmap
        }

        // 計算縮放比例
        val scale = if (width > height) {
            IMAGE_MAX_SIZE.toFloat() / width
        } else {
            IMAGE_MAX_SIZE.toFloat() / height
        }

        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        return bitmap.scale(newWidth, newHeight)
    }


    /**
     * 從內部儲存空間載入已保存的圖片
     */
    private fun loadSavedImage() {
        val savedPath = sharedPreferences.getString(KEY_PROFILE_IMAGE_PATH, null)
        if (savedPath != null) {
            val file = File(savedPath)
            if (file.exists()) {
                try {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    profileImageView.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    // 如果載入失敗，顯示預設圖片
                    profileImageView.setImageResource(R.mipmap.ic_launcher_round)
                }
            } else {
                // 文件不存在，顯示預設圖片
                profileImageView.setImageResource(R.mipmap.ic_launcher_round)
            }
        } else {
            // 沒有保存的圖片，顯示預設圖片
            profileImageView.setImageResource(R.mipmap.ic_launcher_round)
        }
    }

    /**
     * 設定返回鍵處理器
     */
    private fun setupBackPressedHandler() {

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                 dialog()// 關閉所有 Activity 並退出應用程式
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)


    }
    private fun dialog() {
        val builder: AlertDialog.Builder = MaterialAlertDialogBuilder(this)
        builder
            .setMessage("確定要關閉程式")
            .setTitle("程式即將關閉")
            .setPositiveButton("確定") { dialog, which ->
                finishAffinity() // Do something.
            }
            .setNegativeButton("離開") { dialog, which ->
                dialog.dismiss() // Do something else.
            }

            .setCancelable(true)
            .show()

    }

    private fun showSnackbar(message: String, duration: Int) {
        val rootView = findViewById<android.view.View>(R.id.main)
        Snackbar.make(rootView, message, duration).show()
    }

    // 建議在 ViewModel 或 Repository 中執行網路請求

    // 1. 建立 OkHttpClient 實例 (建議單例模式，提升效能)

    // 3. 發送請求並處理回應
    private fun getCurrencyData() {
        client.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(
                    call: Call,
                    e: IOException,
                ) {
                    println("failed: $e")
                }

                override fun onResponse(
                    call: Call,
                    response: Response,
                ) {
//                    println("response: ${response.code}")
//                    response.close()
                    try{
                        val responseBody = response.body?.string()
                        if(responseBody != null) {
                            val jsonObject = JSONObject(responseBody)
                            val twdObject = jsonObject.getJSONObject("twd")

                            Log.v("debug", "第一條訊息"+twdObject.toString())

                            currencyDataClass.clear()

                            val currencyName = mapOf(
                                "usd" to Pair("美元","🇺🇸"),
                                "eur" to Pair("歐元", "🇪🇺"),
                                "jpy" to Pair("日圓", "🇯🇵"),
                                "cny" to Pair("人民幣", "🇨🇳"),
                                "hkd" to Pair("港幣", "🇭🇰"),
                                "krw" to Pair("韓元", "🇰🇷"),
                                "gbp" to Pair("英鎊", "🇬🇧"),
                                "thb" to Pair("泰幣", "🇨🇷"),
                                "cad" to Pair("加拿大幣", "🇨🇦"),
                                "sgd" to Pair("新加坡幣", "🇸🇬"),


                            )
                            for ((code, name) in currencyName) {
                                if (twdObject.has(code)){
                                    val textView = twdObject.getDouble(code)
                                    currencyDataClass.add(CurrencyDataClas(code.uppercase(), name.first, name.second, textView))
                                }
                            }
                            Log.v("debug", "第二條訊息"+currencyDataClass.toString())

                            //更新UI
                            runOnUiThread {
                                adapter.notifyDataSetChanged()
                                showSnackbar("已載入${currencyDataClass.size}", Snackbar.LENGTH_LONG)
                            }

                        }
                    }catch (e: JSONException){
                        e.printStackTrace()

                    }
                    response.close()
                }
            },
        )
    }


}





