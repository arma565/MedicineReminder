package com.medicine.reminder.view.fragment.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.medicine.reminder.R
import com.medicine.reminder.data.model.GlobalFunctions
import com.medicine.reminder.data.model.MedicineReminder
import com.medicine.reminder.databinding.MedicineReminderRowBinding


class MedicineReminderAdapter(
    private val activity: FragmentActivity,
    private var medicineReminderList: List<MedicineReminder>
) :
    RecyclerView.Adapter<MedicineReminderAdapter.AlarmVH>() {

    private lateinit var binding: MedicineReminderRowBinding

    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(list: List<MedicineReminder>) {
        medicineReminderList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmVH {
        binding = DataBindingUtil.bind(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.medicine_reminder_row, parent, false)
        )!!
        return AlarmVH(binding)
    }

    class AlarmVH(binding: MedicineReminderRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: AlarmVH, position: Int) {
        val medicineReminder: MedicineReminder = medicineReminderList[position]
        binding.medicineReminder = medicineReminder


        binding.cardMedicineReminder.setOnClickListener {
            GlobalFunctions.getNavControllerFragmentProperty(activity).navigate(
                R.id.action_homeFragment_to_detailsFragment,
                bundleOf("medicineReminder" to medicineReminder)
            )
        }
    }

    override fun getItemCount(): Int {
        return medicineReminderList.size
    }
}