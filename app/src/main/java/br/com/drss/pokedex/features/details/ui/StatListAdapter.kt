package br.com.drss.pokedex.features.details.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.drss.pokedex.R
import br.com.drss.pokedex.databinding.ItemStatBinding
import br.com.drss.pokedex.features.details.repo.entities.Stat
import java.util.*

class StatListAdapter : ListAdapter<Stat, StatListAdapter.StatViewHolder>(PokemonStatDiff()) {
    inner class StatViewHolder(private val itemStatBinding: ItemStatBinding) :
        RecyclerView.ViewHolder(itemStatBinding.statLabel) {

        fun bind(state: Stat) {
            itemStatBinding.statLabel.apply {
                val upperCasedName = context.resources.getString(R.string.stat_label)
                text = String.format(
                    upperCasedName,
                    state.name.toUpperCase(Locale.getDefault()),
                    state.value
                )
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder {
        val itemStatView =
            ItemStatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StatViewHolder(itemStatView)
    }

    override fun onBindViewHolder(holder: StatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class PokemonStatDiff : DiffUtil.ItemCallback<Stat>() {
    override fun areItemsTheSame(oldItem: Stat, newItem: Stat): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Stat, newItem: Stat): Boolean {
        return oldItem.value == oldItem.value
    }

}
