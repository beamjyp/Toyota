import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView
import android.widget.Toast
import com.example.toyota.R

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gridView = findViewById<GridView>(R.id.gridView)
        val arrayListImage = ArrayList<Int>()
        arrayListImage.add(R.drawable.ic_launcher_background)
        arrayListImage.add(R.drawable.ic_launcher_background)
        arrayListImage.add(R.drawable.ic_launcher_background)
        arrayListImage.add(R.drawable.ic_launcher_background)
        arrayListImage.add(R.drawable.ic_launcher_background)

        val name = arrayOf("Cupcake", "Donut", "Eclair", "Froyo", "Gingerbread")

        val customAdapter = CustomAdapter(this@MenuActivity, arrayListImage, name)

        gridView.adapter = customAdapter

        gridView.setOnItemClickListener { adapterView, parent, position, l ->
            Toast.makeText(this@MenuActivity, "Click on : " + name[position], Toast.LENGTH_LONG).show()
        }
    }

}