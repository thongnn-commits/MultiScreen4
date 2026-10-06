package com.anhba.multiscreen4

import android.app.*
import android.os.Bundle
import android.view.*
import android.webkit.*
import android.widget.*
import android.graphics.Color

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("urls", MODE_PRIVATE) }
    private lateinit var grid: GridLayout
    private val views = ArrayList<WebView>()

    override fun onCreate(b: Bundle?) { super.onCreate(b); buildUi() }

    private fun buildUi() {
        val root = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setBackgroundColor(Color.rgb(20,20,20)) }
        val bar = LinearLayout(this).apply { gravity=Gravity.CENTER_VERTICAL; setPadding(8,6,8,6) }
        val title=TextView(this).apply { text="MultiScreen 4"; setTextColor(Color.WHITE); textSize=18f; setPadding(8,0,18,0) }
        bar.addView(title, LinearLayout.LayoutParams(0,52,1f))
        val settings=Button(this).apply { text="Cài đặt URL"; setOnClickListener{showSettings()} }
        val reload=Button(this).apply { text="↻"; setOnClickListener{views.forEach{it.reload()}} }
        bar.addView(settings); bar.addView(reload)
        root.addView(bar)
        grid=GridLayout(this).apply { rowCount=2; columnCount=2; useDefaultMargins=false }
        root.addView(grid, LinearLayout.LayoutParams(-1,0,1f)); setContentView(root)
        for(i in 0..3) addPanel(i)
    }

    private fun addPanel(i:Int){
        val frame=FrameLayout(this).apply { setBackgroundColor(Color.BLACK) }
        val w=WebView(this).apply { settings.javaScriptEnabled=true; settings.domStorageEnabled=true; settings.mediaPlaybackRequiresUserGesture=false; webViewClient=WebViewClient(); loadUrl(prefs.getString("url$i","https://example.com")!!) }
        views.add(w); frame.addView(w, FrameLayout.LayoutParams(-1,-1))
        val full=Button(this).apply { text="□"; alpha=.75f; setOnClickListener{showFull(i)} }
        frame.addView(full,FrameLayout.LayoutParams(48,48,Gravity.TOP or Gravity.END))
        val lp=GridLayout.LayoutParams().apply { width=0; height=0; columnSpec=GridLayout.spec(i%2,1,1f); rowSpec=GridLayout.spec(i/2,1,1f) }
        grid.addView(frame,lp)
    }

    private fun showSettings(){
        val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(30,10,30,10)}
        val fields=ArrayList<EditText>()
        for(i in 0..3){ val e=EditText(this); e.hint="URL ô ${i+1}"; e.setSingleLine(); e.setText(prefs.getString("url$i","https://example.com")); box.addView(e); fields.add(e) }
        AlertDialog.Builder(this).setTitle("Cấu hình 4 ô").setView(box).setNegativeButton("Hủy",null).setPositiveButton("Lưu"){_,_->
            val ed=prefs.edit(); fields.forEachIndexed{idx,e->ed.putString("url$idx",normalize(e.text.toString()))}; ed.apply(); recreate()
        }.show()
    }
    private fun normalize(s:String)=if(s.startsWith("http://")||s.startsWith("https://"))s else "https://$s"
    private fun showFull(index:Int){
        val w=views[index]; AlertDialog.Builder(this).setView(w).setNegativeButton("Đóng",null).show()
    }
}
