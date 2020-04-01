package com.junseok.bluebox

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.junseok.bluebox.R
import org.jetbrains.anko.find

@SuppressLint("Registered")
class WebViewActivity : AppCompatActivity() {
    private lateinit var webView:WebView

    private lateinit var number:String
    private lateinit var company:String
    private lateinit var uri:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.delivery_item_webview)

        var intent = getIntent()

        number = intent.getStringExtra("number")
        company = intent.getStringExtra("company")

        when(company){
            "CJ대한통운" -> uri = "https://www.cjlogistics.com/ko/tool/parcel/tracking?gnbInvcNo=${number}"
            "우체국택배" -> uri = "https://m.epost.go.kr/postal/mobile/mobile.trace.RetrieveDomRigiTraceList.comm?ems_gubun=E&sid1=${number}&POST_CODE=&mgbn=trace&traceselect=1&target_command=&JspURI=&postNum=${number}&deviceVer=&marketVer=&message="
            "한진택배" -> uri = "https://m.search.daum.net/kakao?w=tot&rtmaxcoll=DHL&DA=ALT&q=${company}%20${number}"
            "로젠택배" -> uri ="https://ilogen.com/mobile/trace_r.asp?gubun=slipno&value1=${number}"
        }

        initView()

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.loadUrl(uri)

    }

    fun initView(){
        webView = find<WebView>(R.id.itemWebView)
    }
}