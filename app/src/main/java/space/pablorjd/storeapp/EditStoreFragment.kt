package space.pablorjd.storeapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import com.google.android.material.snackbar.Snackbar
import space.pablorjd.storeapp.databinding.FragmentEditStoreBinding
import java.util.concurrent.LinkedBlockingQueue

class EditStoreFragment : Fragment() {

    private lateinit var binding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditStoreBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_title_add)



        // para establecer las opciones del menu(actionbar) ej titulo iconos o menu rouserces
        setHasOptionsMenu(true)

        binding.etPhotoUrl.addTextChangedListener {
            loadImg(binding.etPhotoUrl.text.toString())
        }

    }

    private fun loadImg(imgText:String) {
        Glide.with(this)
            .load(imgText)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(binding.imgPhoto)
    }

    /**
     * Function to Create option in action bar ex. Menubuttons
     */

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Function to call action of press button in actionbar
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                hideKeyboard()
                mActivity?.onBackPressedDispatcher?.onBackPressed()
                true
            }

            R.id.action_save -> {
                // se genera una instancia del modelo de datos y se regoge los datos desdde la vista
                val store: StoreEntity = StoreEntity(
                    name = binding.etName.text.toString().trim(),
                    phone = binding.etTel.text.toString().trim(),
                    webSite = binding.etWebSite.text.toString().trim()
                )

                // se genera una nueva instancia de LinkedBlockibQueue
                val queue = LinkedBlockingQueue<Long>()

                // Se genera un hilo para poder ir agregando tienedas sin que la app se quede pegada
                Thread {
                    val id = StoreApplication.dataBase.storeDao().addStore(store)
                    store.id = id
                    queue.add(id)
                }.start()


                mActivity?.addStore(store)
                hideKeyboard()
                with(queue.take()) {

                    Toast.makeText(
                        mActivity,
                        getString(R.string.tienda_agregada_correctamente),
                        Toast.LENGTH_LONG
                    ).show()
                    mActivity?.onBackPressedDispatcher?.onBackPressed()
                }

                true
            }

            else -> return super.onOptionsItemSelected(item)
        }

    }

    private fun hideKeyboard() {
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null) {
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        }
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    /**
     * Function to Delete the Fragment
     */
    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)

        // funcion auxiliar para visualizar el fabicon
        mActivity?.hideFab(true)

        setHasOptionsMenu(false)

        super.onDestroy()
    }


}