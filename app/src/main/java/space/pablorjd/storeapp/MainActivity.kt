package space.pablorjd.storeapp

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    private fun launchEditFragment(args: Bundle? = null) {
        val fragment = EditStoreFragment()

        if (args != null) {
            fragment.arguments = args
        }

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()


        hideFab()
    }

    // se setea el recyclerview
    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, resources.getInteger(R.integer.main_columns))
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
    override fun onClick(storeId: Long) {
        val args = Bundle()
        args.putLong(getString(R.string.arg_id), storeId)

        launchEditFragment(args)
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
        mAdapter.update(storeEntity)
    }

    // elimina la tienda
    override fun onDeleteStore(storeEntity: StoreEntity) {
        // val items = arrayOf("Eliminar", "Llamar", "Ir al sitio Web")
        val items = resources.getStringArray(R.array.array_options_item)
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_options_title)
            .setItems(items, DialogInterface.OnClickListener { dialogInterface, i ->
                when (i) {
                    0 -> confirmDelete(storeEntity)
                    1 -> callToNumber(storeEntity.phone)
                    2 -> openBrowser(storeEntity.webSite)
                    else -> null
                }
            })
            .show()

    }

    /**
     * Funcion para ejecutar la apertura del marcador de num
     */
    private fun callToNumber(phone: String) {
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }

        // se verifica que en el dispositivo exista app compatible con el tipo de intent
        startIntent(callIntent)

    }

    private fun openBrowser(url: String) {
        if (url.isEmpty()) {
            Toast.makeText(this, R.string.main_error_no_website, Toast.LENGTH_LONG).show()
        } else {
            val browserInten = Intent().apply {
                action = Intent.ACTION_VIEW
                // cuando se pasa una url esta debe de contener el http o https
                data = Uri.parse(url)
            }
            startIntent(browserInten)
        }
    }

    private fun confirmDelete(storeEntity: StoreEntity) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(
                R.string.dialog_delete_confirm,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    val queue = LinkedBlockingQueue<StoreEntity>()
                    Thread {
                        StoreApplication.dataBase.storeDao().deleteStore(storeEntity)
                        queue.add(storeEntity)
                    }.start()

                    mAdapter.delete(queue.take())
                })
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .show()
    }

    private fun startIntent(intent: Intent) {
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, R.string.main_error_no_resolve, Toast.LENGTH_LONG).show()
        }
    }

    // funcion auxiliar para visualizar el fab
    override fun hideFab(isVisible: Boolean) {
        if (isVisible) mBinding.fab.show() else mBinding.fab.hide()
    }
}