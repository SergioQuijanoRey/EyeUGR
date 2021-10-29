package com.project.agilugr

import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.view.GestureDetectorCompat
import com.project.agilugr.ui.navigation.NavigationDirector
import com.project.agilugr.ui.navigation.NavigationMapper
import com.project.agilugr.ui.theme.AgilUGRTheme
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MainActivity : ComponentActivity() {

    // APIs que vamos a consumir para tomar los datos del backend
    val focus_api = MockFocusAPI.getMockFocusAPI()

    // Tomamos el director de navegacion para que lance la interfaz grafica
    val navigation_director = NavigationDirector(focus_api = focus_api)

    // Detector de gestor
    private lateinit var mDetector: GestureDetectorCompat


    // Funcion principal
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Llamamos al onCreate del parent
        super.onCreate(savedInstanceState)



        // Establecemos la UI de la aplicacion
        // Esto tambien crea el director de navegacion
        setContent {
            AgilUGRTheme {

                // Usamos el director de navegacion para lanzar la interfaz grafica
                navigation_director.buildNavigationAndStartUI()
            }
        }

        // Establecemos el detector de gestos
        mDetector = GestureDetectorCompat(this, MyGestureListener(navigation_director))
    }

    // Detectamos los gestos usando la clase privada que hemos desarrollado
    override fun onTouchEvent(event: MotionEvent): Boolean {
        mDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }


    // Gestion simple de gestos
    private class MyGestureListener(val navigationDirector: NavigationDirector): GestureDetector.SimpleOnGestureListener() {

        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onDown(event: MotionEvent): Boolean {
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            this.navigationDirector.navigate(NavigationMapper.PERFIL_MODE)
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val result = false
            try {
                val diffY = e2!!.y - e1!!.y
                val diffX = e2.x - e1.x


                if (this.navigationDirector.getCurrentView()==NavigationMapper.MAIN_VIEW){
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                this.navigationDirector.navigate(NavigationMapper.FOCUS_MODE_SELECTOR)
                            } else {
                                //Poner la vista del calendario
                            }
                        }
                    } else {
                        if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffY > 0) {
                                this.navigationDirector.navigate(NavigationMapper.MAIN_VIEW)
                            } else {
                                this.navigationDirector.navigate(NavigationMapper.PERFIL_MODE)
                            }
                        }
                    }
                }

                if (this.navigationDirector.getCurrentView()==NavigationMapper.PERFIL_MODE) {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            this.navigationDirector.navigate(NavigationMapper.MAIN_VIEW)

                        }
                    }
                }

                if (this.navigationDirector.getCurrentView()==NavigationMapper.FOCUS_MODE_SELECTOR) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            this.navigationDirector.navigate(NavigationMapper.MAIN_VIEW)

                        }
                    }
                }















            }catch (exception: Exception) {
                exception.printStackTrace()
            }

            return result
        }

    }

}

