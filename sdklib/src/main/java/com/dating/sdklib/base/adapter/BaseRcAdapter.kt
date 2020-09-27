package com.dating.sdklib.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

@Suppress("UNCHECKED_CAST")
abstract class BaseRcAdapter<T, VH: BaseRcAdapter.BaseRcViewHolder>(
    val context: Context
): RecyclerView.Adapter<VH>() {

    private val mList = mutableListOf<T>()
    val inflater: LayoutInflater = LayoutInflater.from(context)

    var singleItemClick: OnSingleItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = inflateView(parent, getViewLayout(viewType))
        return BaseRcViewHolder(view) as VH
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: VH, position: Int) = onBindVH(holder, position)

    abstract fun getViewLayout(viewType: Int): Int

    abstract fun onBindVH(holder: VH, position: Int)

    val data: MutableList<T>
        get() = mList

    //刷新数据
    fun refreshData(data: List<T>?) {
        mList.clear()
        data?.run {
            mList.addAll(data)
        }
        notifyDataSetChanged()
    }

    //加载更多数据
    fun loadMoreData(data: List<T>?) {
        data?.run {
            mList.addAll(data)
        }
        notifyDataSetChanged()
    }

    fun addItemData(data: T) {
        mList.add(data)
        notifyDataSetChanged()
    }

    fun get(position: Int) = if (position >= 0 && position < mList.size) mList[position] else null

    open class BaseRcViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    interface OnSingleItemClick {
        fun clickItem(position: Int)
    }

    fun inflateView(parent: ViewGroup, resId: Int): View = LayoutInflater.from(context).inflate(resId, parent, false)
}