package com.example.connectuni

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView


class CustomAdapter (private val dataSet:ArrayList<*>,context: Context):
ArrayAdapter<Any?>(context,R.layout.dependantlist, dataSet) {

    val checkedSelection : ArrayList<String> = ArrayList()

    private class ViewHolder {
        lateinit var txtName: TextView
        lateinit var checkBox: CheckBox
    }

    override fun getCount(): Int {
        return dataSet.size
    }

    override fun getItem(position: Int): DependantData {
        return dataSet[position] as DependantData
    }

    override fun getView(
        position: Int, convertView: View?, parent: ViewGroup
    ): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        val result: View
        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView =
                LayoutInflater.from(parent.context).inflate(R.layout.dependantlist, parent, false)
            viewHolder.txtName = convertView.findViewById(R.id.txtName)
            viewHolder.checkBox = convertView.findViewById(R.id.checkBox)
            result = convertView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }

        val item: DependantData = getItem(position)
        viewHolder.txtName.text = item.fullName
        viewHolder.checkBox.isChecked = item.checked

        viewHolder.checkBox.setOnCheckedChangeListener()
        { compoundButton: CompoundButton, isChecked: Boolean ->
            if (isChecked == true) {
                viewHolder.checkBox.isChecked = true
                checkedSelection.add(item.fullName)
                //add into arraylist
            } else {
                viewHolder.checkBox.isChecked = false
                checkedSelection.removeAt(position)
            }
            return@setOnCheckedChangeListener
        }

        return result
    }
}