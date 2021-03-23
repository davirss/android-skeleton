package br.com.drss.pokedex.features.home.ui.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.drss.pokedex.R
import br.com.drss.pokedex.databinding.ItemSummaryBinding
import br.com.drss.pokedex.extensions.getColorResource
import br.com.drss.pokedex.extensions.getIconId
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonSummary
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonType
import com.bumptech.glide.Glide
import java.util.*

class PokemonSummaryListAdapter(val itemClickAction: (pokemonSummary: PokemonSummary) -> Unit) :
    ListAdapter<PokemonSummary, PokemonSummaryListAdapter.PokemonItem>(PokemonSummaryDiff()) {

    inner class PokemonItem(private val summaryBinding: ItemSummaryBinding) :
        RecyclerView.ViewHolder(
            summaryBinding.root
        ) {
        fun bind(pokemonSummary: PokemonSummary) {
            val pokemonNumberFormat = itemView.context.resources.getString(R.string.pokemon_number)

            summaryBinding.idTextView.text =
                String.format(pokemonNumberFormat, pokemonSummary.number)

            summaryBinding.pokemonName.text =
                pokemonSummary.name.capitalize(Locale.getDefault())

            Glide
                .with(summaryBinding.frontSprite)
                .load(pokemonSummary.artwork)
                .centerCrop()
                .into(summaryBinding.frontSprite)

            bindTypeSlots(pokemonSummary.types)

            summaryBinding.root.setOnClickListener {
                itemClickAction(pokemonSummary)
            }
        }

        private fun bindTypeSlots(types: List<PokemonType>) {
            summaryBinding.firstSlotTypeImageView.apply {
                imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        types.first().getColorResource()
                    )
                )
                setImageResource(
                    types.first().getIconId()
                )
            }

            if (types.size > 1) {
                summaryBinding.se.apply {
                    setImageResource(types[1].getIconId())
                    imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            types[1].getColorResource()
                        )
                    )
                }
            } else {
                summaryBinding.se.setImageDrawable(null)
                summaryBinding.se.imageTintList = null
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonItem {
        val view = ItemSummaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PokemonItem(view)
    }

    override fun onBindViewHolder(holder: PokemonItem, position: Int) {
        holder.bind(getItem(position))
    }
}


class PokemonSummaryDiff : DiffUtil.ItemCallback<PokemonSummary>() {

    override fun areItemsTheSame(oldItem: PokemonSummary, newItem: PokemonSummary): Boolean {
        return oldItem.name == newItem.name

    }

    override fun areContentsTheSame(oldItem: PokemonSummary, newItem: PokemonSummary): Boolean {
        return oldItem == newItem

    }

}