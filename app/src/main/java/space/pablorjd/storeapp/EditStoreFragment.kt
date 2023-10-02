package space.pablorjd.storeapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import space.pablorjd.storeapp.databinding.FragmentEditStoreBinding

class EditStoreFragment : Fragment() {

    private lateinit var binding: FragmentEditStoreBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditStoreBinding.inflate(inflater, container, false)
        return binding.root
    }


}