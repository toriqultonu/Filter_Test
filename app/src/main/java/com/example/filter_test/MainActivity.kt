package com.example.filter_test

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.SubMenu
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    private var selectedReport = "All"
    private var selectedFollowUp = "All"
    private val selectedBodyRegions = mutableSetOf<String>()
    private var menu: Menu? = null
    private var popupWindow: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize with all body regions
        selectedBodyRegions.addAll(listOf("All", "Neck", "Arms", "Back", "Legs", "Posture"))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Report items
            R.id.report_all -> {
                selectedReport = "All"
                updateReportCheckmarks(item)
                return true
            }
            R.id.report_processing -> {
                selectedReport = "Processing"
                updateReportCheckmarks(item)
                return true
            }
            R.id.report_completed -> {
                selectedReport = "Completed"
                updateReportCheckmarks(item)
                return true
            }
            R.id.report_in_review -> {
                selectedReport = "In Review"
                updateReportCheckmarks(item)
                return true
            }

            // Follow-Up items
            R.id.follow_up_all -> {
                selectedFollowUp = "All"
                updateFollowUpCheckmarks(item)
                return true
            }
            R.id.follow_up_yes -> {
                selectedFollowUp = "Yes"
                updateFollowUpCheckmarks(item)
                return true
            }
            R.id.follow_up_no -> {
                selectedFollowUp = "No"
                updateFollowUpCheckmarks(item)
                return true
            }

            // Body Region menu
            R.id.menu_body_region -> {
                showBodyRegionPopup(item)
                return true
            }

            // Done button
            R.id.menu_done -> {
                showSelections()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showBodyRegionPopup(item: MenuItem) {
        // Inflate the popup layout
        val popupView = layoutInflater.inflate(R.layout.popup_body_region, null)

        // Create the popup window
        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Get all checkboxes
        val checkBoxAll = popupView.findViewById<CheckBox>(R.id.checkbox_all)
        val checkBoxNeck = popupView.findViewById<CheckBox>(R.id.checkbox_neck)
        val checkBoxArms = popupView.findViewById<CheckBox>(R.id.checkbox_arms)
        val checkBoxBack = popupView.findViewById<CheckBox>(R.id.checkbox_back)
        val checkBoxLegs = popupView.findViewById<CheckBox>(R.id.checkbox_legs)
        val checkBoxPosture = popupView.findViewById<CheckBox>(R.id.checkbox_posture)

        val allCheckboxes = listOf(
            checkBoxNeck,
            checkBoxArms,
            checkBoxBack,
            checkBoxLegs,
            checkBoxPosture
        )

        // Set initial states
        checkBoxAll.isChecked = selectedBodyRegions.contains("All")
        checkBoxNeck.isChecked = selectedBodyRegions.contains("Neck")
        checkBoxArms.isChecked = selectedBodyRegions.contains("Arms")
        checkBoxBack.isChecked = selectedBodyRegions.contains("Back")
        checkBoxLegs.isChecked = selectedBodyRegions.contains("Legs")
        checkBoxPosture.isChecked = selectedBodyRegions.contains("Posture")

        // Handle "All" checkbox
        checkBoxAll.setOnCheckedChangeListener { _, isChecked ->
            allCheckboxes.forEach { it.isChecked = isChecked }
            updateSelectedBodyRegions(checkBoxAll, allCheckboxes)
        }

        // Handle individual checkboxes
        allCheckboxes.forEach { checkbox ->
            checkbox.setOnCheckedChangeListener { _, _ ->
                updateSelectedBodyRegions(checkBoxAll, allCheckboxes)
            }
        }

        // Show popup
        val location = IntArray(2)
        findViewById<View>(android.R.id.content).getLocationInWindow(location)
        popupWindow?.showAtLocation(
            findViewById(android.R.id.content),
            Gravity.TOP or Gravity.END,
            0,
            location[1] + 100
        )

        // Dismiss popup when clicked outside
        popupWindow?.setOnDismissListener {
            popupWindow = null
        }
    }

    private fun updateSelectedBodyRegions(checkBoxAll: CheckBox, allCheckboxes: List<CheckBox>) {
        selectedBodyRegions.clear()

        if (checkBoxAll.isChecked) {
            selectedBodyRegions.add("All")
        }

        allCheckboxes.forEach { checkbox ->
            if (checkbox.isChecked) {
                selectedBodyRegions.add(checkbox.text.toString())
            }
        }

        // Update "All" checkbox state
        if (allCheckboxes.all { it.isChecked }) {
            checkBoxAll.isChecked = true
            if (!selectedBodyRegions.contains("All")) {
                selectedBodyRegions.add("All")
            }
        } else {
            checkBoxAll.isChecked = false
            selectedBodyRegions.remove("All")
        }
    }

    private fun updateReportCheckmarks(selectedItem: MenuItem) {
        menu?.findItem(R.id.menu_report)?.subMenu?.let { subMenu ->
            for (i in 0 until subMenu.size()) {
                subMenu.getItem(i).isChecked = false
            }
        }
        selectedItem.isChecked = true
    }

    private fun updateFollowUpCheckmarks(selectedItem: MenuItem) {
        menu?.findItem(R.id.menu_follow_up)?.subMenu?.let { subMenu ->
            for (i in 0 until subMenu.size()) {
                subMenu.getItem(i).isChecked = false
            }
        }
        selectedItem.isChecked = true
    }

    private fun showSelections() {
        val bodyRegionsText = if (selectedBodyRegions.isEmpty()) {
            "No regions selected"
        } else {
            selectedBodyRegions.joinToString(", ")
        }

        val message = "Selected Options:\n" +
                "Report: $selectedReport\n" +
                "Follow-Up: $selectedFollowUp\n" +
                "Body Regions: $bodyRegionsText"

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        popupWindow?.dismiss()
        Log.d("tonu", message)
    }

    override fun onPause() {
        super.onPause()
        popupWindow?.dismiss()
    }
}