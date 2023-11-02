package com.example.labexam03

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity2 : AppCompatActivity() {

    private val appointmentFragment = AppointmentFragment()
    private val patientRecordsFragment = PatientRecordsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


        val appointmentbtn: Button = findViewById(R.id.appointmentbtn)
        val recordsbtn: Button = findViewById(R.id.recordsbtn)



        appointmentbtn.setOnClickListener {
            loadAppointments()
        }

        recordsbtn.setOnClickListener {
            loadRecords()
        }
    }


    private fun loadAppointments(){
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        if (fragment == null){
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainer,
                appointmentFragment).commit()
        }else{
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer,
                appointmentFragment).commit()
        }

    }
    private fun loadRecords(){
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        if (fragment == null){
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainer,
                patientRecordsFragment).commit()
        }else{
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer,
                patientRecordsFragment).commit()
        }

    }
}