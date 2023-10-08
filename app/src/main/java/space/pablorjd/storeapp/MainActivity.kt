package space.pablorjd.storeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import space.pablorjd.storeapp.databinding.ActivityMainBinding
import java.util.concurrent.LinkedBlockingQueue

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {


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

       /* mBinding.btnSave.setOnClickListener {
            val name = mBinding.etName.text.toString().trim()
            Log.i("nombre", name)
            val storeEntity = StoreEntity(name = mBinding.etName.text.toString().trim())
            Thread {
                val inserted = StoreApplication.dataBase.storeDao().addStore(storeEntity)
                Log.i("storeinserted", inserted.toString())
            }.start()
            mAdapter.add(storeEntity)
        } */

        mBinding.fab.setOnClickListener {
            launchEditFragment()
        }
        setupRecyclerView()
    }

    // funcion básica para llamar un fragment
    private fun launchEditFragment() {
        val fragment = EditStoreFragment()

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain,fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        //mBinding.fab.hide()
        hideFab()
    }

    // se setea el recyclerview
    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, 2)
        getAllStore()
        mBinding.recyclerView.apply {
            setHasFixedSize(true) // como el recycler tiene una altura fija se debe indicar eso en la creacion para indicar que no cambia de tamaño
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    // se llama a todas las tiendas que estan en dbsqli
    private fun getAllStore() {
        val queue = LinkedBlockingQueue<MutableList<StoreEntity>>()
        Thread {
            val stores = StoreApplication.dataBase.storeDao().allStore()
            Log.i("store", stores.toString())
            queue.add(stores)
        }.start()

        mAdapter.setStore(queue.take())

        /* CoroutineScope(Dispatchers.IO).launch {
             val stores = StoreApplication.dataBase.storeDao().getAllStore()
             runOnUiThread {
                 mAdapter.setStore(stores)
             }
         } */
    }

    /*
    OnClickListener Intefaces
     */
    override fun onClick(storeEntity: StoreEntity) {
        TODO("Not yet implemented")
    }

    // guarda si la tienda es favorita
    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite
        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread {
            StoreApplication.dataBase.storeDao().updateStore(storeEntity)
            queue.add(storeEntity)
        }.start()

        mAdapter.update(queue.take())

    }

    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {
        TODO("Not yet implemented")
    }

    // elimina la tienda
    override fun onDeleteStore(storeEntity: StoreEntity) {
        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread {
            StoreApplication.dataBase.storeDao().deleteStore(storeEntity)
            queue.add(storeEntity)
        }.start()

        mAdapter.delete(queue.take())
    }

    // funcion auxiliar para visualizar el fab
    override fun hideFab(isVisible: Boolean) {
        if (isVisible) mBinding.fab.show() else mBinding.fab.hide()
    }
}