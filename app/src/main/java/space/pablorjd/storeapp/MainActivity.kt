package space.pablorjd.storeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import space.pablorjd.storeapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        /*
        codigo para ver una tienda
         */

        mBinding.btnSave.setOnClickListener {
            val storeEntity = StoreEntity(name = mBinding.etName.text.toString().trim())
            mAdapter.add(storeEntity)
        }

        setupRecyclerView()


    }

    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, 2)

        mBinding.recyclerView.apply {
            setHasFixedSize(true) // como el recycler tiene una altura fija se debe indicar eso en la creacion para indicar que no cambia de tamaño
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    /*
    OnClickListener Intefaces
     */
    override fun onClick(storeEntity: StoreEntity) {
        TODO("Not yet implemented")
    }
}