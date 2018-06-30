package crocodile8008.currencies

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.createActivityComponent(this)
        setContentView(R.layout.activity_main)
    }

    override fun onDestroy() {
        super.onDestroy()
        App.instance.destroyMainActivityComponent()
        App.instance.refWatcher.watch(this)
    }
}
