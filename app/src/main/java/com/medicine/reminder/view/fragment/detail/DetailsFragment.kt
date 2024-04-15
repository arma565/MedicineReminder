package com.medicine.reminder.view.fragment.detail

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.medicine.reminder.viewmodel.MedicineReminderViewModel
import com.medicine.reminder.R
import com.medicine.reminder.data.model.GlobalFunctions
import com.medicine.reminder.data.model.MedicineReminder
import com.medicine.reminder.data.services.MedicineService
import com.medicine.reminder.databinding.FragmentDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private lateinit var medicineReminder: MedicineReminder
    private val viewModel: MedicineReminderViewModel by viewModels()
    private lateinit var owner: LifecycleOwner

    override fun onAttach(context: Context) {
        super.onAttach(context)
        owner = this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.bind(inflater.inflate(R.layout.fragment_details, container, false))!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) medicineReminder =
            arguments?.getParcelable(
                "medicineReminder",
                MedicineReminder::class.java
            )!!
        medicineReminder = arguments?.getParcelable("medicineReminder")!!
        binding.medicineReminder = medicineReminder
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController: NavController =
            GlobalFunctions.getNavControllerFragmentProperty(requireActivity())
        val mainActivity = requireActivity() as AppCompatActivity
        binding.detailsToolbar.title = ""
        mainActivity.setSupportActionBar(binding.detailsToolbar)


        (mainActivity as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.details_menu, menu)

                menu.findItem(R.id.item_deleteDetails).setOnMenuItemClickListener {
                    val alert = AlertDialog.Builder(requireActivity())
                    alert.setTitle(requireActivity().resources.getString(R.string.delete))
                    alert.setMessage(requireActivity().getString(R.string.are_you_sure))
                    alert.setPositiveButton(requireActivity().resources.getString(R.string.yes)) { _, _ ->
                        val alarmManager: AlarmManager =
                            requireContext().getSystemService(Activity.ALARM_SERVICE) as AlarmManager
                        val intentMedicine = Intent(requireContext(), MedicineService::class.java)
                        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                            requireContext(),
                            medicineReminder.id,
                            intentMedicine,
                            PendingIntent.FLAG_IMMUTABLE
                        )
                        alarmManager.cancel(pendingIntent)
                        viewModel.delete(medicineReminder).observe(owner) { res ->
                            if (res <= 0) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.unsuccessful),
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@observe
                            }
                            GlobalFunctions.getResult(requireActivity() , res.toLong())
                        }
                    }
                    alert.setNegativeButton(requireActivity().resources.getString(R.string.no)) { _, _ -> }
                    alert.show()


                    true
                }

                menu.findItem(R.id.item_editDetails).setOnMenuItemClickListener {
                    navController.navigate(
                        R.id.action_detailsFragment_to_editFragment,
                        bundleOf("medicineReminder" to medicineReminder)
                    )
                    true
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }

        })

        binding.imgClose.setOnClickListener {
            GlobalFunctions.getResult(requireActivity(),1)
        }
    }
}