package com.unifor.comprovaentrega

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.unifor.comprovaentrega.ui.theme.ComprovaEntregaTheme

class MainActivity : ComponentActivity() {

    private val entregas = mutableStateListOf<Delivery>()
    private var telaAtual by mutableStateOf(AppScreen.HOME)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ComprovaEntregaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (telaAtual) {

                        AppScreen.HOME -> {
                            HomeScreen(
                                entregas = entregas,
                                onNovaEntregaClick = {
                                    telaAtual = AppScreen.NEW_DELIVERY
                                },
                                onEntregaClick = { entrega ->
                                    // futura ação: tela de detalhes
                                    println("Entrega selecionada: ${entrega.id}")
                                }
                            )
                        }

                        AppScreen.NEW_DELIVERY -> {
                            NewDeliveryScreen(
                                onBackClick = {
                                    telaAtual = AppScreen.HOME
                                },
                                // AJUSTE AQUI: Agora recebemos 6 parâmetros, incluindo a foto
                                onSaveClick = { codigo, dest, obs, lat, lng, foto ->

                                    val novaEntrega = Delivery(
                                        codigoPedido = codigo,
                                        destinatario = dest,
                                        observacao = obs,
                                        latitude = lat,    // Salva GPS
                                        longitude = lng,   // Salva GPS
                                        photoPath = foto   // SALVA A FOTO REAL 🔥
                                    )

                                    entregas.add(0, novaEntrega)
                                    telaAtual = AppScreen.HOME
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}