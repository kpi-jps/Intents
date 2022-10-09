package ifsp.ads.pdm.jp.intents

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import ifsp.ads.pdm.jp.intents.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var amb : ActivityMainBinding
    private lateinit var urlArl : ActivityResultLauncher<Intent>
    private lateinit var permissionToCallArl : ActivityResultLauncher<String>
    private lateinit var takeImageArl : ActivityResultLauncher<Intent>
    private lateinit var chooserArl : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        supportActionBar?.subtitle = "MainActivity"

        urlArl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult())
        { result : ActivityResult ->
            if(result.resultCode == RESULT_OK)
                amb.urlTv.text = result.data?.getStringExtra(Constants.URL) ?: ""
        }

        permissionToCallArl = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted!!) callNumber(true)
            else Toast.makeText(
                this@MainActivity,
                "Permissão necessária para a execução",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        takeImageArl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result : ActivityResult ->
            if(result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                Toast.makeText(this, imageUri.toString(), Toast.LENGTH_SHORT).show()
                imageUri?.let {
                    amb.urlTv.text = it.toString()
                }
                val imageViewerIntent = Intent(ACTION_VIEW, imageUri)
            }
        }

        amb.entrarUrlBt.setOnClickListener {
            val urlActivityIntent = Intent(this, UrlActivity::class.java)
            //val urlActivityIntent = Intent("URL_ACTIVITY")
            urlActivityIntent.putExtra(Constants.URL, amb.urlTv.text.toString())
            urlArl.launch(urlActivityIntent)
        }
    }

    //coloca o menu na Actionbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true;
    }
    //trata das escolhas das opções de menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.viewMi -> {
                //Abrir o navegador na url digitada
                val url = Uri.parse(amb.urlTv.text.toString())
                val browserIntent = Intent(ACTION_VIEW, url)
                startActivity(browserIntent)
                true
            }
            R.id.dialMi -> {
                callNumber(false)
                true
            }
            R.id.callMi -> {
                //verificar a versão do android
                //se superior ou igual a versão marshmallow verificar se tem a permissão e solicitar se necessário
                //caso contrário fazer a chamada
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkSelfPermission(CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        callNumber(true)
                    } else {
                        permissionToCallArl.launch(CALL_PHONE)
                    }
                } else {
                    callNumber(true)
                }
                true
            }
            R.id.pickMi -> {
                val takeImageIntent = Intent(ACTION_PICK)
                val imageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                takeImageIntent.setDataAndType(Uri.parse(imageDirectory), "image/*")
                //startActivity(takeImageIntent)
                takeImageArl.launch(takeImageIntent);
                true
            }
            R.id.chooserMi -> {
                val intentChooseApp = Intent(ACTION_CHOOSER)
                val infoIntent = Intent(ACTION_VIEW, Uri.parse(amb.urlTv.text.toString()))
                intentChooseApp.putExtra(EXTRA_TITLE, "Escolha seu navegador")
                intentChooseApp.putExtra(EXTRA_INTENT, infoIntent)
                startActivity(intentChooseApp)
                true
            }
            else -> { false }
        }
    }

    private fun callNumber(call : Boolean) {
        val uri = Uri.parse("tel: ${amb.urlTv.text.toString()}")
        val intent = Intent(if(call) ACTION_CALL else ACTION_DIAL)
        intent.data = uri
        startActivity(intent)
    }

}