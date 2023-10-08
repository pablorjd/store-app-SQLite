package space.pablorjd.storeapp

import android.content.Context
import android.os.Bundle
import android.text.Editable
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
    private var isEditMode: Boolean = false
    private var mStoreEntity: StoreEntity? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditStoreBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(getString(R.string.arg_id), 0)

        if (id != null && id != 0L) {
            isEditMode = true
            getStore(id)

        } else {
            isEditMode = false
            mStoreEntity = StoreEntity(name = "", phone = "", photoUrl = "")

        }

        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_title_add)


        // para establecer las opciones del menu(actionbar) ej titulo iconos o menu rouserces
        setHasOptionsMenu(true)

        binding.etPhotoUrl.addTextChangedListener {
            loadImg(binding.etPhotoUrl.text.toString())
        }

    }

    private fun getStore(id: Long) {
        val queue = LinkedBlockingQueue<StoreEntity?>()
        Thread {
            mStoreEntity = StoreApplication.dataBase.storeDao().getStoreById(id)
            queue.add(mStoreEntity)
        }.start()

        queue.take()?.let {
            setUIStore(it!!)
        }

    }

    private fun setUIStore(it: StoreEntity) {
        with(binding) {
            //etName.setText( it.name )
            //etTel.setText( it.phone )
            //etWebSite.setText( it.webSite )
            //etPhotoUrl.setText( it.photoUrl )
            etName.text = it.name.editable()
            etTel.text = it.phone.editable()
            etWebSite.text = it.webSite.editable()
            etPhotoUrl.text = it.photoUrl.editable()
            loadImg(it.photoUrl)
        }
    }

    // funcion de extension
    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun loadImg(imgText: String) {
        Glide.with(this).load(imgText).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop()
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
                if (mStoreEntity != null) {
                    with(mStoreEntity!!) {
                        name = binding.etName.text.toString().trim()
                        phone = binding.etTel.text.toString().trim()
                        webSite = binding.etWebSite.text.toString().trim()
                        photoUrl = binding.etPhotoUrl.text.toString().trim()
                    }
                    // se genera una nueva instancia de LinkedBlockibQueue
                    val queue = LinkedBlockingQueue<StoreEntity>()
                    // Se genera un hilo para poder ir agregando tienedas sin que la app se quede pegada
                    Thread {
                        if (isEditMode) {
                            StoreApplication.dataBase.storeDao().updateStore(mStoreEntity!!)
                        } else {
                            val id = StoreApplication.dataBase.storeDao().addStore(mStoreEntity!!)
                            mStoreEntity!!.id = id
                        }
                        queue.add(mStoreEntity)
                    }.start()

                    with(queue.take()) {
                        if (isEditMode) {
                            mActivity?.updateStore(this)
                            hideKeyboard()
                            Toast.makeText(
                                mActivity,
                                getString(R.string.edit_store_message_update_success),
                                Toast.LENGTH_LONG
                            ).show()
                        }else {
                            mActivity?.addStore(this)
                            hideKeyboard()
                            Toast.makeText(
                                mActivity,
                                getString(R.string.tienda_agregada_correctamente),
                                Toast.LENGTH_LONG
                            ).show()
                            mActivity?.onBackPressedDispatcher?.onBackPressed()
                        }

                    }
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