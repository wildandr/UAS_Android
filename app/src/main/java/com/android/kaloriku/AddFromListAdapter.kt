package com.android.kaloriku

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.kaloriku.data.DataMakanan
import com.android.kaloriku.databinding.MakananListForAddBinding

typealias OnBtnAdd = (DataMakanan) -> Unit

class AddFromListAdapter(private var listFood: List<DataMakanan>, private val onBtnAdd: OnBtnAdd) :
    RecyclerView.Adapter<AddFromListAdapter.ItemFoodViewHolder>() {

    inner class ItemFoodViewHolder(private val binding: MakananListForAddBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DataMakanan) {
            with(binding) {
                txtNamaMakanan.text = data.namaMakanan
                txtJumlahKalori.text = data.kalori.toString()
                txtJumlah.text = data.jumlah.toString()
                txtSatuan.text = data.satuan

                btnAddToDataHarian.setOnClickListener {
                    onBtnAdd(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFoodViewHolder {
        val binding = MakananListForAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemFoodViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listFood.size
    }

    override fun onBindViewHolder(holder: ItemFoodViewHolder, position: Int) {
        holder.bind(listFood[position])
    }

    fun submitList(newList: List<DataMakanan>) {
        listFood = newList
        notifyDataSetChanged()
    }
}
