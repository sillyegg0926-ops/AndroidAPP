package com.example.myapplication.adapter

    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.TextView
    import androidx.recyclerview.widget.RecyclerView
    import com.example.myapplication.CurrencyDataClas
    import com.example.myapplication.R

// 1. 定義你的資料
class MyAdapter(private val currencyDataClass: List<CurrencyDataClas>, private val onItemClickListener: OnItemClickListener? = null):
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int, item: String)
        fun onItemLongClick(position: Int, item: String)
        fun editItem(position: Int, item: String)
        fun deleteItem(position: Int, item: String)
    }
        // 2. 定義 ViewHolder，它會持有每個清單項目的 View
        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val currencyCodeText: TextView = itemView.findViewById(R.id.textView_title1)
            val currencyNameText: TextView = itemView.findViewById(R.id.textView_title2)
            val textView: TextView = itemView.findViewById(R.id.textView_title3)
        }

        // 3. 創建 ViewHolder
        // 每次需要一個新的清單項目 View 時，這個方法會被呼叫
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_view, parent, false)
            return MyViewHolder(view)
        }

        // 4. 將資料綁定到 ViewHolder
        // 當清單項目 View 準備好時，這個方法會被呼叫，將資料填入到 View 中
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.currencyCodeText.text = currencyDataClass[position].currencyCodeText
            holder.currencyNameText.text = currencyDataClass[position].currencyNameText
            holder.textView.text = String.format("%.2f", currencyDataClass[position].textView)

            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemClick(position, currencyDataClass[position])
            }

            // 設置長按監聽器
            holder.itemView.setOnLongClickListener {
                onItemClickListener?.onItemLongClick(position, currencyDataClass[position])
                true
            }

        }

        // 5. 告訴 RecyclerView 有多少個項目
        override fun getItemCount(): Int {
            return currencyDataClass.size
        }
    }

