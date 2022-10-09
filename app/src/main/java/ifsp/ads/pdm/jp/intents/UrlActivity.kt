package ifsp.ads.pdm.jp.intents

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ifsp.ads.pdm.jp.intents.databinding.ActivityUrlBinding

class UrlActivity : AppCompatActivity(){
    private lateinit var aub : ActivityUrlBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aub = ActivityUrlBinding.inflate(layoutInflater)
        setContentView(aub.root)
        supportActionBar?.subtitle = "UrlActivity"

        val pastUrl = intent.getStringExtra(Constants.URL) ?: ""

        if (pastUrl.isNotEmpty()) aub.urlEt.setText(pastUrl)

        /*
        o modo kotlin de fazer o que foi feito na linha acima:
        urlAnterior.takeIf { it.isNotEmpty() }.also {
            aub.urlEt.setText(urlAnterior)
        }
        */

        aub.entrarUrlBt.setOnClickListener {
            val returnIntent = Intent()
            returnIntent.putExtra(Constants.URL, aub.urlEt.text.toString())
            setResult(RESULT_OK, returnIntent)
            finish()
        }
    }

}