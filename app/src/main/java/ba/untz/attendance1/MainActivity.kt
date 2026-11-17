package ba.untz.attendance1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import ba.untz.attendance1.ui.theme.Attendance1Theme

class MainActivity : ComponentActivity() {
    // LAB6, zadaci 8 i 9 - predvidjanja prije pokretanja:
    // 1. Home: onPause, onStop; povratak: onStart, onResume.
    // 2. Rotacija: onPause, onStop, onDestroy, zatim onCreate, onStart,
    //    onResume. ViewModel istog navigacijskog unosa ostaje.
    // 3. Back na listu uklanja ViewModel predavanja; jos jedan Back zavrsava
    //    aktivnost: onPause, onStop, onDestroy.
    // PROVJERENO na emulatoru: Home -> onPause, onStop, onStart, onResume;
    // rotacija -> onPause, onStop, onDestroy, onCreate, onStart, onResume;
    // Back iz predavanja -> ViewModel cleared, zatim Back s liste ->
    // onPause, onStop, onDestroy. onStop moze stici sa zakasnjenjem.
    // Stanje                              Home + povratak    Rotacija
    // prozor i pretraga (ViewModel)       ostaje             ostaje
    // rucna prijava (ViewModel)           ostaje             ostaje
    // nacrt forme (rememberSaveable)      ostaje             ostaje
    // otvorena kartica (rememberSaveable) ostaje             ostaje
    // Napomena: u nasem Lab4 kartica koristi rememberSaveable, a ne remember
    // iz tabele zadatka. S remember bi pri rotaciji bila zatvorena.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lab6", "Activity onCreate")
        enableEdgeToEdge()

        setContent {
            Attendance1Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    AppNavigation(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lab6", "Activity onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lab6", "Activity onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lab6", "Activity onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lab6", "Activity onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lab6", "Activity onDestroy")
    }
}

