package com.project.agilugr.ui.views

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.project.agilugr.FocusAPI

/** Clase que representa la vista de la pantalla desde la que, seleccionamos una configuracion
 * concreta de Focus Mode y entramos en una sesion de Focus Mode*/
class FocusModeSelector(val focus_api: FocusAPI, val navController: NavController) {
    @Composable
    fun getView(){
        Text("Primeras pruebas")
        Button(onClick = {navController.navigate(route = "focus_mode_session")}) {
            Text(text = "Clica para entrar al focus mode")
        }
    }
}