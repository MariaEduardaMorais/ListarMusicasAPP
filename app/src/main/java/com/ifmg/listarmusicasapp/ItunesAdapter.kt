package com.ifmg.listarmusicasapp

import com.ifmg.listarmusicasapp.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class ItunesAdapter : RecyclerView.Adapter<ItunesAdapter.ViewHolder>() {

    private var results: List<ItunesResult> = listOf()

    fun setResults(results: List<ItunesResult>) {
        this.results = results
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_itunes_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]

        holder.trackNameTextView.text = result.trackName
        holder.artistNameTextView.text = result.artistName
        holder.collectionNameTextView.text = "Álbum: ${result.collectionName ?: "Indisponível"}"

        val releaseDate = result.releaseDate
        if (releaseDate != null) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            try {
                val date = inputFormat.parse(releaseDate)
                val formattedDate = outputFormat.format(date)
                holder.releaseDateTextView.text = "Data de Lançamento: $formattedDate"
            } catch (e: Exception) {
                e.printStackTrace()
                holder.releaseDateTextView.text = "Data de Lançamento: Indisponível"
            }
        } else {
            holder.releaseDateTextView.text = "Data de Lançamento: Indisponível"
        }
    }

    override fun getItemCount(): Int {
        return results.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackNameTextView: TextView = itemView.findViewById(R.id.trackNameTextView)
        val artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)
        val collectionNameTextView: TextView = itemView.findViewById(R.id.collectionNameTextView)
        val releaseDateTextView: TextView = itemView.findViewById(R.id.releaseDateTextView)
    }
}

