import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView
import android.widget.Toast
import com.example.toyota.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gridView = findViewById<GridView>(R.id.gridView) as GridView

        val arrayListImage = ArrayList<Int>()

        arrayListImage.add(com.google.android.material.R.drawable.design_ic_visibility)
        arrayListImage.add(com.google.android.material.R.drawable.design_ic_visibility)
        arrayListImage.add(com.google.android.material.R.drawable.design_ic_visibility)
        arrayListImage.add(com.google.android.material.R.drawable.design_ic_visibility)
        arrayListImage.add(com.google.android.material.R.drawable.design_ic_visibility)
        arrayListImage.add(com.google.android.material.R.drawable.design_ic_visibility)

        val name = arrayOf("Cupcake", "Donut", "Eclair", "Froyo", "Gingerbread,sad")

        val customAdapter = CustomAdapter(this@MainActivity, arrayListImage, name)

        gridView.adapter = customAdapter

        gridView.setOnItemClickListener { adapterView, parent, position, l ->
            Toast.makeText(this@MainActivity, "Click on : " + name[position], Toast.LENGTH_LONG).show()
        }
    }

}