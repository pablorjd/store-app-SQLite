package space.pablorjd.storeapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import space.pablorjd.storeapp.databinding.ItemStoreBinding
import java.util.Objects

class StoreAdapter(
    private var stores: MutableList<StoreEntity>,
    private var listener: OnClickListener
) :
    RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    private lateinit var mcontext: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mcontext = parent.context
        val view = LayoutInflater.from(mcontext).inflate(R.layout.item_store, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = stores.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = stores[position]
        with(holder) {
            setListener(store)
            binding.tvName.text = store.name
            binding.cbFavorite.isChecked = store.isFavorite
            Glide.with(mcontext)
                 .load(store.photoUrl)
                 .diskCacheStrategy(DiskCacheStrategy.ALL)
                 .centerCrop()
                 .into(binding.imgPhoto)
        }
    }

    fun add(storeEntity: StoreEntity) {
        if ( !stores.contains(storeEntity) ) {
            stores.add(storeEntity)
            notifyItemInserted(stores.size-1)
        }

    }

    fun setStore(stores: MutableList<StoreEntity>) {
        this.stores = stores
        notifyDataSetChanged()
    }

    fun update(store: StoreEntity) {
        val index = stores.indexOf(store)
        if (index != -1) {
            stores.set(index, store)
            notifyItemChanged(index)
        }
    }

    fun delete(store: StoreEntity) {
        val index = stores.indexOf(store)
        if (index != -1) {
            stores.remove(store)
            notifyItemRemoved(index)
        }
    }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemStoreBinding.bind(view)

        fun setListener(storeEntity: StoreEntity) {
            binding.root.setOnClickListener {
                listener.onClick(storeEntity)
            }

            binding.cbFavorite.setOnClickListener {
                listener.onFavoriteStore(storeEntity)
            }

            binding.root.setOnLongClickListener {
                listener.onDeleteStore(storeEntity)
                true
            }
        }
    }
}