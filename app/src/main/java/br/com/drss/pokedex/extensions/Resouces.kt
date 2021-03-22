package br.com.drss.pokedex.extensions

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.IntegerRes
import androidx.fragment.app.Fragment
import br.com.drss.pokedex.R
import br.com.drss.pokedex.features.home.repository.domain.entities.PokemonType

inline fun <reified T : Class<*>> T.getId(resourceName: String): Int {
    return try {
        val idField = getDeclaredField(resourceName)
        idField.getInt(idField)
    } catch (e: Exception) {
        e.printStackTrace()
        -1
    }
}

fun PokemonType.getIconId(): Int {
    return R.drawable::class.java.getId("ic_$name")
}

fun PokemonType.getColorResource(): Int {
    return R.color::class.java.getId(name)
}

fun Fragment.getInteger(@IntegerRes resId: Int) = resources.getInteger(resId)

@ColorInt
fun Context.getColorFromAttr(@AttrRes attrColor: Int
): Int {
    val typedArray = theme.obtainStyledAttributes(intArrayOf(attrColor))
    val textColor = typedArray.getColor(0, 0)
    typedArray.recycle()
    return textColor
}