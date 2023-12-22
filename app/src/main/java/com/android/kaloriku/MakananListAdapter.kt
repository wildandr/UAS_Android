package com.android.kaloriku

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.kaloriku.databinding.HistoryMakananListBinding
import com.android.kaloriku.roomDb.DataHarian

typealias OnClickFood = (DataHarian) -> Unit
typealias OnClickEdit = (DataHarian) -> Unit
typealias OnClickDelete = (DataHarian) -> Unit

class MakananListAdapter(
    private var listFood: List<DataHarian>,
    private val onClickFood: OnClickFood,
    private val onClickEdit: OnClickEdit,
    private val onClickDelete: OnClickDelete) :
    RecyclerView.Adapter<MakananListAdapter.ItemFoodViewHolder>() {

    inner class ItemFoodViewHolder(private val binding: HistoryMakananListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DataHarian) {
            with(binding) {
                txtNamaMakanan.text = data.namaMakanan
                txtJumlahKalori.text = data.kalori.toString()
                txtJumlah.text = data.jumlah.toString()
                txtSatuan.text = data.satuan
                waktu.text = data.waktu

                itemView.setOnClickListener {
                    onClickFood(data)
                }

                btnEditMakanan.setOnClickListener {
                    onClickEdit(data)
                }

                btnDeleteMakanan.setOnClickListener {
                    onClickDelete(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFoodViewHolder {
        val binding = HistoryMakananListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemFoodViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listFood.size
    }

    override fun onBindViewHolder(holder: ItemFoodViewHolder, position: Int) {
        holder.bind(listFood[position])
    }

    fun submitList(newList: List<DataHarian>) {
        listFood = newList
        notifyDataSetChanged()
    }
}