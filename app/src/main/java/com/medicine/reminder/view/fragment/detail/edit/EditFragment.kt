package com.medicine.reminder.view.fragment.detail.edit

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.medicine.reminder.viewmodel.MedicineReminderViewModel
import com.medicine.reminder.R
import com.medicine.reminder.data.model.GlobalFunctions
import com.medicine.reminder.data.model.MedicineReminder
import com.medicine.reminder.data.services.MedicineService
import com.medicine.reminder.databinding.FragmentEditBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime

@Suppress("DEPRECATION")
@AndroidEntryPoint
class EditFragment : Fragment() {
    private lateinit var binding : FragmentEditBinding
    private val viewModel : MedicineReminderViewModel by viewModels()
    private lateinit var owner: LifecycleOwner
    private lateinit var medicineReminder: MedicineReminder

    override fun onAttach(context: Context) {
        super.onAttach(context)
        owner = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.bind(inflater.inflate(R.layout.fragment_edit,container,false))!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) medicineReminder = arguments?.getParcelable("medicineReminder",MedicineReminder::class.java)!!
        medicineReminder = arguments?.getParcelable("medicineReminder")!!
        binding.medicineReminder = medicineReminder
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dt = DateTime()
        var hour = dt.hourOfDay
        var min = dt.minuteOfHour

        binding.timePicker.setOnTimeChangedListener { _, h, m ->
            hour = h
            min = m
        }

        binding.btnEdit.setOnClickListener {
            try {
                val title: String = binding.edtMedicine.text.toString()
                if (title.isEmpty()) throw NullPointerException()
                val medicineReminder = MedicineReminder(medicineReminder.id,title, hour, min)
                viewModel.update(medicineReminder).observe(owner) { res ->
                    if (res <= 0) {
                        throw Error()
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.getMedicineReminderList().collect { medicineList ->
                            if (medicineList.isEmpty()) return@collect
                            CoroutineScope(Dispatchers.Main).launch {
                                val alarmManager: AlarmManager = requireContext().getSystemService(Activity.ALARM_SERVICE) as AlarmManager
                                val intentMedicine = Intent(requireContext(), MedicineService::class.java)
                                val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                                    requireContext(),
                                    medicineReminder.id,
                                    intentMedicine,
                                    PendingIntent.FLAG_IMMUTABLE
                                )
                                alarmManager.cancel(pendingIntent)
                                alarmManager.set(
                                    AlarmManager.RTC_WAKEUP,
                                    dt.millis,
                                    pendingIntent
                                )
                                GlobalFunctions.getResult(
                                    requireActivity(),
                                    res.toLong()
                                )
                            }
                        }
                    }
                }
            } catch (error: Error) {
                Toast.makeText(
                    activity, getString(R.string.unsuccessful), Toast.LENGTH_LONG
                ).show()
            } catch (e: NullPointerException) {
                binding.edtMedicine.error = getString(R.string.medicine_can_not_be_empty)
            } catch (e: Exception) {
                Toast.makeText(
                    activity, "${e.message}", Toast.LENGTH_LONG
                ).show()
            }
        }

    }

}