package com.medicine.reminder.view.fragment.add

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.medicine.reminder.R
import com.medicine.reminder.data.model.GlobalFunctions
import com.medicine.reminder.data.model.MedicineReminder
import com.medicine.reminder.data.services.MedicineService
import com.medicine.reminder.databinding.FragmentAddBinding
import com.medicine.reminder.viewmodel.MedicineReminderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import org.joda.time.DateTime

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AddFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding
    private val viewModel: MedicineReminderViewModel by viewModels()
    private lateinit var owner: LifecycleOwner
    private var uri : Uri? = null

    private val registerForActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if (result.resultCode == Activity.RESULT_OK){
            try {
                if (result.data == null) return@registerForActivityResult
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) uri = result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI , Uri::class.java)!!
                uri = result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)!!
                binding.txtRingtone.text = getString(R.string.ring,uri.toString())
            }catch (e : NullPointerException){
                return@registerForActivityResult
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        owner = this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dt = DateTime()
        var hour = dt.hourOfDay
        var min = dt.minuteOfHour
        binding.timePicker.hour = hour
        binding.timePicker.minute = min

        binding.timePicker.setOnTimeChangedListener { _, h, m ->
            hour = h
            min = m
        }

        binding.btnSetRingtone.setOnClickListener {
            registerForActivity.launch(Intent(RingtoneManager.ACTION_RINGTONE_PICKER))
        }


        binding.btnSave.setOnClickListener {
            try {
                val title: String = binding.edtMedicine.text.toString()
                if (title.isEmpty()) throw NullPointerException()
                val medicineReminder = MedicineReminder(title, hour, min)
                viewModel.insert(medicineReminder).observe(owner) { res ->
                    if (res <= 0) {
                        throw Error()
                    }
                    CoroutineScope(IO).launch {
                        viewModel.getMedicineReminderList().collect { medicineList ->
                            if (medicineList.isEmpty()) return@collect
                            CoroutineScope(Main).launch {
                                val alarmManager: AlarmManager = requireActivity().getSystemService(Activity.ALARM_SERVICE) as AlarmManager
                                val intentMedicineService = Intent(activity, MedicineService::class.java)
                                if (uri == null){
                                    uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                                    binding.txtRingtone.text = getString(R.string.ring,uri.toString())
                                }
                                intentMedicineService.putExtra("ringtoneKey" , uri)
                                intentMedicineService.putExtra("medicineReminder",medicineReminder)
                                val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                                    activity,
                                    medicineList.last().id,
                                    intentMedicineService,
                                    PendingIntent.FLAG_IMMUTABLE
                                )
                                alarmManager.set(
                                    AlarmManager.RTC_WAKEUP,
                                    dt.millis,
                                    pendingIntent
                                )
                                GlobalFunctions.getResult(
                                    requireActivity(),
                                    res
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