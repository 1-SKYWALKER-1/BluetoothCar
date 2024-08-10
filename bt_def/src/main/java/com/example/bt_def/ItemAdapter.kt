package com.example.bt_def

import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bt_def.databinding.ListItemBinding

class ItemAdapter(private val listener: Listener,val AdapterType: Boolean) :ListAdapter<List_Item, ItemAdapter.MyHolder>(Comparator()) {
    private var oldCheckBox: CheckBox? = null


    class MyHolder(view: View, private val adapter: ItemAdapter, private val listener: Listener,val AdapterType: Boolean) : RecyclerView.ViewHolder(view){
        private val b = ListItemBinding.bind(view)
        private var item1 : List_Item? = null
        init{
            b.checkBox.setOnClickListener {
                item1?.let { it1 -> listener.onClick(it1) }
                adapter.SelectCheckBox(it as CheckBox)
            }
            itemView.setOnClickListener {
                if(AdapterType){
                    try{
                        item1?.device?.createBond()
                    }catch(e: SecurityException){

                    }
                } else {
                    item1?.let { it1 -> listener.onClick(it1) }
                    adapter.SelectCheckBox(b.checkBox)
                }
            }
        }
        fun bind(item: List_Item) = with(b){
            item1 = item
            checkBox.visibility = if(AdapterType) View.GONE else VISIBLE
            try{
                deviceName.text = item.device.name
                macAdress.text = item.device.address
            } catch(e: SecurityException){}

            if(item.isChecked) adapter.SelectCheckBox(checkBox)
        }
    }
    class Comparator : DiffUtil.ItemCallback<List_Item>(){
        override fun areItemsTheSame(oldItem: List_Item, newItem: List_Item): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: List_Item, newItem: List_Item): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent,false)
    return MyHolder(view,this, listener, AdapterType)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.bind(getItem(position))
    }
    fun SelectCheckBox(checkBox: CheckBox){
        oldCheckBox?.isChecked = false
        oldCheckBox = checkBox
        oldCheckBox?.isChecked = true
    }
    interface Listener {
        fun onClick(device: List_Item){

        }
    }
}