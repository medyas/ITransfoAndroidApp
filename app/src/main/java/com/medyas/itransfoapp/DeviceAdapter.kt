package com.medyas.itransfoapp

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import java.util.ArrayList



class DeviceAdapter : RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private var mDataset: List<DeviceList>  = ArrayList<DeviceList>()
    private var items: MutableList<DeviceList> = ArrayList<DeviceList>()

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // each data item is just a string in this case
         var name: TextView? = null
         var company: TextView? = null
         var ref: TextView? = null
         var layout: LinearLayout? = null

        init {
            name = view.findViewById<TextView>(R.id.itemName)
            company = view.findViewById<TextView>(R.id.itemCompany)
            ref = view.findViewById<TextView>(R.id.itemRef)
            layout = view.findViewById<LinearLayout>(R.id.layout)
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    constructor (myDataset: List<DeviceList>) {
        mDataset = myDataset
        items.addAll(myDataset)
    }

    fun filter(text: String) {
        var text = text
        items.clear()
        if (text.isEmpty() || text.equals("")) {
            items.addAll(mDataset)
        } else {
            text = text.toLowerCase()
            for (item in mDataset) {
                if (item.device_name.toLowerCase().contains(text) || item.company_name.toLowerCase().contains(text)) {
                    items.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): DeviceAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.custom_listview, parent, false)

        return ViewHolder(itemView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        var ml:DeviceList = items[position]
        holder.name!!.setText(ml.device_name)
        holder.company!!.setText(ml.company_name)
        holder.ref!!.setText(ml.device_ref)

        holder.layout!!.setOnClickListener { view ->
            val intent = Intent(view.context, DeviceInfo::class.java)
            intent.putExtra("device_uid", items[position].device_uid)
            intent.putExtra("device_name", items[position].device_name)
            view.context.startActivity(intent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return items.size
    }
}
