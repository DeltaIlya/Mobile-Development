package com.example.lr12_cosmetics

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemsAdapter(
    private val items: List<Item>,
    private val context: Context
) : RecyclerView.Adapter<ItemsAdapter.MyViewHolder>()
{

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val image: ImageView = view.findViewById(R.id.itemListImage)
        val title: TextView = view.findViewById(R.id.itemListTitle)
        val desc: TextView = view.findViewById(R.id.itemListDescription)
        val price: TextView = view.findViewById(R.id.itemListPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_in_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        val item = items[position]
        holder.title.text = item.title
        holder.desc.text = item.desc
        holder.price.text = "${item.price} ₽"

        var imageLoaded = false

        if (item.imageUri.isNotEmpty())
        {
            // Пробуем как URI (для товаров из галереи)
            if (item.imageUri.startsWith("content://") || item.imageUri.startsWith("file://"))
            {
                try
                {
                    holder.image.setImageURI(Uri.parse(item.imageUri))
                    imageLoaded = true
                }
                catch (e: Exception)
                {
                    // Не URI — пробуем дальше
                }
            }

            // Пробуем как имя drawable-ресурса (для seed-товаров)
            if (!imageLoaded)
            {
                val imageId = context.resources.getIdentifier(
                    item.imageUri,
                    "drawable",
                    context.packageName
                )
                if (imageId != 0)
                {
                    holder.image.setImageResource(imageId)
                    imageLoaded = true
                }
            }
        }

        // Фолбэк на иконку-заглушку
        if (!imageLoaded)
            holder.image.setImageResource(R.drawable.ic_cosmetics)

        // Клик по карточке → переход на детальный экран
        holder.itemView.setOnClickListener()
        {
            val intent = Intent(context, ItemActivity::class.java)
            intent.putExtra("ITEM_ID", item.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size
}