package br.com.drss.pokedex.features.details.ui

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

/**
 * Glide [RequestListener] that generates a [Palette] using the downloaded image.
 */
class GlidePaletteGeneratorListener(val onPalletReady: (Palette) -> Unit) :
    RequestListener<Drawable> {

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        //Do nothing
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        resource?.let {
            Palette.Builder(it.toBitmap()).generate {
                it?.let {
                    onPalletReady(it)
                }
            }
        }
        return false
    }

}