package com.example.appblocker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView

class AppListAdapter(private val context: Context, private val appList: List<String>) : BaseAdapter() {

    private val selectedApps = mutableSetOf<String>()

    override fun getCount(): Int = appList.size

    override fun getItem(position: Int): Any = appList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        val appCheckBox = view.findViewById<CheckBox>(R.id.appCheckBox)
        val appNameTextView = view.findViewById<TextView>(R.id.appNameTextView)

        val appName = appList[position]
        appNameTextView.text = appName

        appCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedApps.add(appName)
            } else {
                selectedApps.remove(appName)
            }
        }

        return view
    }

    fun getSelectedApps(): Set<String> = selectedApps
}
