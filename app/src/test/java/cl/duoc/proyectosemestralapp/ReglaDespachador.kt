package cl.duoc.proyectosemestralapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class ReglaDespachador : TestWatcher() {
    private val despachador = StandardTestDispatcher()

    override fun starting(descripcion: Description) {
        Dispatchers.setMain(despachador)
    }

    override fun finished(descripcion: Description) {
        Dispatchers.resetMain()
    }
}
