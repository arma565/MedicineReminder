package com.medicine.reminder.view.fragment.home

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medicine.reminder.R
import com.medicine.reminder.data.model.GlobalFunctions
import com.medicine.reminder.data.services.MedicineService
import com.medicine.reminder.databinding.FragmentHomeBinding
import com.medicine.reminder.view.fragment.home.adapter.MedicineReminderAdapter
import com.medicine.reminder.viewmodel.MedicineReminderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: MedicineReminderViewModel by viewModels()
    private lateinit var medicineReminderAdapter: MedicineReminderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myActivity = requireActivity() as AppCompatActivity
        myActivity.setSupportActionBar(binding.toolbarHomeFragment)

        (myActivity as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.home_menu, menu)
                val searchItem: MenuItem = menu.findItem(R.id.item_search)
                searchItem.setOnMenuItemClickListener {
                    val searchView: SearchView = searchItem.actionView as SearchView
                    searchView.queryHint = getString(R.string.search_here)
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            if (newText.isNullOrEmpty()) {
                                showAll()
                                return false
                            }
                            CoroutineScope(IO).launch {
                                viewModel.getMedicineReminderList().collect {
                                    val filteredList = it.filter { mr ->
                                        mr.title?.lowercase(Locale.ROOT)?.trim()
                                            ?.contains(newText)!!
                                    }

                                    CoroutineScope(Main).launch {
                                        medicineReminderAdapter.setFilteredList(filteredList)
                                        binding.recMain.setHasFixedSize(true)
                                        binding.recMain.adapter = medicineReminderAdapter
                                        binding.recMain.layoutManager = LinearLayoutManager(
                                            requireContext(),
                                            RecyclerView.VERTICAL,
                                            false
                                        )
                                    }
                                }
                            }
                            return true
                        }

                    })
                    true
                }

                menu.findItem(R.id.item_clearAll).setOnMenuItemClickListener {
                    val alarmManager: AlarmManager = requireContext().getSystemService(Activity.ALARM_SERVICE) as AlarmManager
                    val alert = AlertDialog.Builder(requireContext())
                    alert.setTitle(R.string.delete)
                    alert.setMessage(getString(R.string.are_you_sure))
                    alert.setPositiveButton(getString(R.string.yes)) { _, _ ->
                        CoroutineScope(IO).launch {
                            viewModel.getMedicineReminderList().collect{medicineReminderList->
                                medicineReminderList.forEach {medicineReminder ->
                                    val intentMedicine = Intent(requireContext(), MedicineService::class.java)
                                    val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                                        requireContext(),
                                        medicineReminder.id,
                                        intentMedicine,
                                        PendingIntent.FLAG_IMMUTABLE
                                    )
                                    alarmManager.cancel(pendingIntent)
                                }
                            }
                        }
                        viewModel.deleteAll()
                    }
                    alert.setNegativeButton(getString(R.string.no)) { _, _ -> }
                    alert.show()
                    true
                }


            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }

        })

        showAll()

        binding.btnFab.setOnClickListener {
            GlobalFunctions.getNavControllerFragmentProperty(requireActivity())
                .navigate(R.id.action_homeFragment_to_addFragment)
        }

    }

    private fun showAll() {
        CoroutineScope(IO).launch {
            viewModel.getMedicineReminderList().collect { medicineList ->
                CoroutineScope(Main).launch {
                    medicineReminderAdapter =
                        MedicineReminderAdapter(requireActivity(), medicineList)
                    binding.recMain.adapter = medicineReminderAdapter
                    binding.recMain.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                }
            }
        }
    }
}