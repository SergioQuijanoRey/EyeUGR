package com.project.agilugr.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.agilugr.FocusAPI
import com.project.agilugr.MockFocusAPI
import com.project.agilugr.backend.MockPerfilAPI
import com.project.agilugr.backend.MockedProfile
import com.project.agilugr.ui.views.FocusModeSelector
import com.project.agilugr.ui.views.FocusModeSessionView
import com.project.agilugr.ui.views.IndexSelector
import com.project.agilugr.ui.views.PerfilMode
import kotlin.time.ExperimentalTime

/** Clase que maneja toda la navegacion de nuestra aplicacion */
class NavigationDirector(val focus_api: FocusAPI){

    /** Variable que vamos a usar para navegar por las distintas vistas */
    var navController: NavController? = null
    //var currentView: String? = "main_view"
    var currentView: NavigationMapper? = NavigationMapper.MAIN_VIEW
    /**
     * Construye la navigacion para nuestra aplicacion
     *
     * Trabaja con un NavController y construye un NavHost compuesto de las distintas vistas de
     * nuestra aplicacion.
     *
     * Gracias al parametro startDestination, la funcion devuelve la vista desde la que parte
     * nuestra aplicacion, y con ello comienza la interfaz grafica de nuestra aplicacion
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    @ExperimentalTime
    @Composable
    fun buildNavigationAndStartUI(){

        // El controlador que necesitamos para controlar en detalle la navegacion
        // No estamos entrando en detalle, pero modificando esta variable podemos acceder a ello
        // Ademas, podemos usar este atributo para navegar a otras vistas
        this.navController = rememberNavController()

        // El NavHost define las vistas que disponemos y como navegamos entre ellas
        NavHost(

            // El controlador que vamos a usar para la navegacion
            navController = this.navController as NavHostController,

            // La vista inicial
            startDestination = NavigationMapper.MAIN_VIEW.route
        ){
            // Vista principal, índice de selección de otras vistas
            composable(route = NavigationMapper.MAIN_VIEW.route) {
                // TODO add MockedProfile correctly
                IndexSelector( MockedProfile.getMockIndexAPI(), navController = navController as NavHostController).getView()
            }
            // Vista del perfil
            composable(route = NavigationMapper.PERFIL_MODE.route) {
                // TODO add MockedProfile correctly
                PerfilMode( MockPerfilAPI.getMockPerfilAPI(), navController = navController as NavHostController).getView()
            }
            // Vista del selector de configuraciones del focus mode
            composable(route = NavigationMapper.FOCUS_MODE_SELECTOR.route){
                FocusModeSelector(MockFocusAPI.getMockFocusAPI(), navController = navController as NavHostController).getView()
            }

            // Vista desde dentro de una sesion de focus mode
            composable(route = NavigationMapper.FOCUS_MODE_SESSION.route){
                FocusModeSessionView(MockFocusAPI.getMockFocusAPI(), navController = navController as NavHostController).getView()
            }
        }
    }

    /** Navega a un destino dado */
    fun navigate(destination: NavigationMapper){
        //Navegamos a esta vista
        this.navController!!.navigate(destination.route)
        //Actualizamos la vista actual en la que nos encontramos
        this.currentView = destination
    }

    @JvmName("getCurrentView1")
    fun getCurrentView(): NavigationMapper? {
        return this.currentView
    }
}

