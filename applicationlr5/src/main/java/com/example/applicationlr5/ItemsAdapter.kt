package com.example.applicationlr5

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemsAdapter(
    private val items: List<Item>,
    private val context: Context
) : RecyclerView.Adapter<ItemsAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.itemListImage)
        val title: TextView = view.findViewById(R.id.itemListTitle)
        val desc: TextView = view.findViewById(R.id.itemListDescription)
        val price: TextView = view.findViewById(R.id.itemListPrice)
        val status: TextView = view.findViewById(R.id.itemListStatus)
        val button: Button = view.findViewById(R.id.itemListButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_in_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.desc.text = item.desc
        holder.price.text = "${item.price} ₽"

        // Загрузка картинки из drawable по имени
        val imageId = context.resources.getIdentifier(
            item.image,
            "drawable",
            context.packageName
        )
        if (imageId != 0) {
            holder.image.setImageResource(imageId)
        } else {
            holder.image.setImageResource(R.drawable.ic_launcher_foreground)
        }

        // Контрольное задание вариант 6: статус "Под заказ" / "В наличии"
        if (item.price > 150000) {
            holder.status.text = "Под заказ"
            holder.status.setTextColor(android.graphics.Color.parseColor("#C62828"))
        } else {
            holder.status.text = "В наличии"
            holder.status.setTextColor(android.graphics.Color.parseColor("#2E7D32"))
        }

        // Клик по кнопке "Посмотреть" → переход на детальный экран
        holder.button.setOnClickListener {
            val intent = Intent(context, ItemActivity::class.java)
            intent.putExtra("itemTitle", item.title)
            intent.putExtra("itemText", item.text)
            intent.putExtra("itemImage", item.image)
            intent.putExtra("itemPrice", item.price)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size
}