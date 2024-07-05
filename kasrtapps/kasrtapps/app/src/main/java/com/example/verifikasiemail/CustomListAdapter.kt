package com.example.verifikasiemail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CustomListAdapter : ListAdapter<Report, CustomListAdapter.ListViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val report = getItem(position)
        holder.tvNama.text = report.nama
        holder.tvNamaL.text = report.namaLengkap
        holder.tvAlamat.text = report.alamat
        holder.tvNohp.text = report.noHp
        Glide.with(holder.itemView.context).load(report.imageUrl).into(holder.itemAvatar)
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemAvatar: ImageView = itemView.findViewById(R.id.itemAvatar)
        val tvNama: TextView = itemView.findViewById(R.id.Nama)
        val tvNamaL: TextView = itemView.findViewById(R.id.NamaL)
        val tvAlamat: TextView = itemView.findViewById(R.id.Alamat)
        val tvNohp: TextView = itemView.findViewById(R.id.Nohp)
    }

    class DiffCallback : DiffUtil.ItemCallback<Report>() {
        override fun areItemsTheSame(oldItem: Report, newItem: Report): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Report, newItem: Report): Boolean {
            return oldItem == newItem
        }
    }
}
