package space.pablorjd.storeapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import space.pablorjd.storeapp.databinding.FragmentEditStoreBinding

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
        return when(item.itemId) {
            android.R.id.home -> {
                mActivity?.onBackPressedDispatcher?.onBackPressed()
                true
            }
            R.id.action_save -> {
                Snackbar.make(binding.root, "Tienda Agregada Correctamente",Snackbar.LENGTH_LONG).show()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }

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