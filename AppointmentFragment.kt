package com.example.labexam03

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import com.example.labexam03.database.AppDatabase
import com.example.labexam03.database.Appointment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.regex.Pattern

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AppointmentFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_appointment, container, false)

        val editTextDOB = view.findViewById<EditText>(R.id.editTextDOB)
        val buttonDOBPicker = view.findViewById<Button>(R.id.buttonDOBPicker)
        val editTextAppointmentDate = view.findViewById<EditText>(R.id.editTextAppointmentDate)
        val buttonAppointmentDate = view.findViewById<Button>(R.id.buttonAppointmentDate)
        val editTextName = view.findViewById<EditText>(R.id.editTextText2)
        val editTextPhone = view.findViewById<EditText>(R.id.editTextPhone)
        val editTextAge = view.findViewById<EditText>(R.id.editTextNumber)
        val submitButton = view.findViewById<Button>(R.id.submitbtn)
        val spinnerTimeSlot = view.findViewById<Spinner>(R.id.spinner2)
        val spinnerAppointmentType = view.findViewById<Spinner>(R.id.spinner3)

        val timeSlotList = resources.getStringArray(R.array.timeSlots)
        val appointmentTypeList = resources.getStringArray(R.array.SelectDoctor)

        val timeSlotAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, timeSlotList)
        timeSlotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTimeSlot.adapter = timeSlotAdapter

        val appointmentTypeAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, appointmentTypeList)
        appointmentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAppointmentType.adapter = appointmentTypeAdapter


        buttonDOBPicker.setOnClickListener {
            showDatePickerDialogForDOB(editTextDOB)
        }

        buttonAppointmentDate.setOnClickListener {
            showDatePickerDialogForAppointmentDate(editTextAppointmentDate)
        }

        submitButton.setOnClickListener {
            val name = editTextName.text.toString()
            val phone = editTextPhone.text.toString()
            val age = editTextAge.text.toString()
            val dob = editTextDOB.text.toString()
            val appointmentDate = editTextAppointmentDate.text.toString()
            val selectedGender = getSelectedGender()
            val selectedTimeSlot = spinnerTimeSlot.selectedItem.toString()
            val selectedAppointmentType = spinnerAppointmentType.selectedItem.toString()


            if (name.isEmpty()) {
                editTextName.error = "Please enter the patient's name."
            } else if (phone.isEmpty()) {
                editTextPhone.error = "Please enter the patient's phone number."
            } else if (!isValidPhoneNumber(phone)) {
                editTextPhone.error = "Please enter a valid phone number."
            } else if (age.isEmpty()) {
                editTextAge.error = "Please enter the patient's age."
            } else if (!isValidAge(age)) {
                editTextAge.error = "Please enter a valid age."
            } else if (dob.isEmpty()) {
                editTextDOB.error = "Please select the date of birth."
            } else if (appointmentDate.isEmpty()) {
                editTextAppointmentDate.error = "Please select the appointment date."
            } else if (selectedGender == null) {
                Toast.makeText(requireContext(), "Please select a gender.", Toast.LENGTH_SHORT).show()
            }else if (selectedTimeSlot.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a time slot.", Toast.LENGTH_SHORT).show()
            } else if (selectedAppointmentType.isEmpty()) {
                Toast.makeText(requireContext(), "Please select an appointment type.", Toast.LENGTH_SHORT).show()
            } else {

                val appointmentDao = AppDatabase.getDatabase(requireContext()).appointmentDao()

                val appointment = Appointment(
                    id = null,
                    patientName = name,
                    patientPhone = phone,
                    patientAge = age.toInt(),
                    dateOfBirth = dob,
                    appointmentDate = appointmentDate,
                    gender = selectedGender,
                    timeSlot = selectedTimeSlot,
                    appointmentType = selectedAppointmentType
                )


                Log.d("Database", "Adding Appointment: $appointment")
                CoroutineScope(Dispatchers.IO).launch {
                    appointmentDao.insertAppointment(appointment)
                }


                Toast.makeText(requireContext(), "Appointment submitted successfully.", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    private fun showDatePickerDialogForDOB(editTextDOB: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dobDatePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                editTextDOB.setText(selectedDate)
            },
            year,
            month,
            day
        )
        dobDatePickerDialog.datePicker.maxDate = calendar.timeInMillis
        dobDatePickerDialog.show()
    }

    private fun showDatePickerDialogForAppointmentDate(editTextAppointmentDate: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val appointmentDatePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                editTextAppointmentDate.setText(selectedDate)
            },
            year,
            month,
            day
        )

        val calendarForMinDate = Calendar.getInstance()
        calendarForMinDate.add(Calendar.DAY_OF_MONTH, 1)
        appointmentDatePickerDialog.datePicker.minDate = calendarForMinDate.timeInMillis

        appointmentDatePickerDialog.show()
    }
    private fun getSelectedGender(): String? {
        val radioButtonMale = view?.findViewById<RadioButton>(R.id.radioButtonMale)
        val radioButtonFemale = view?.findViewById<RadioButton>(R.id.radioButtonFemale)

        if (radioButtonMale?.isChecked == true) {
            return "Male"
        } else if (radioButtonFemale?.isChecked == true) {
            return "Female"
        }

        return null
    }


    private fun isValidPhoneNumber(phone: String): Boolean {

        val pattern = Pattern.compile("^[0-9]{10}$")
        return pattern.matcher(phone).matches()
    }

    private fun isValidAge(age: String): Boolean {
        try {
            val ageValue = age.toInt()
            return ageValue in 1..150
        } catch (e: NumberFormatException) {
            return false
        }
    }





    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AppointmentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}