package com.unifor.comprovaentrega

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.unifor.comprovaentrega.data.local.AppDatabase
import com.unifor.comprovaentrega.data.local.entity.Delivery
import com.unifor.comprovaentrega.ui.details.DeliveryDetailScreen
import com.unifor.comprovaentrega.ui.home.HomeScreen
import com.unifor.comprovaentrega.ui.newdelivery.NewDeliveryScreen
import com.unifor.comprovaentrega.ui.theme.ComprovaEntregaTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val db by lazy { AppDatabase.getDatabase(this) }
    private val dao by lazy { db.deliveryDao() }

    private var telaAtual by mutableStateOf<NavScreen>(NavScreen.Home)
    private var entregaSelecionada by mutableStateOf<Delivery?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val entregas by dao.getAllDeliveries().collectAsState(initial = emptyList())
            val scope = rememberCoroutineScope()

            ComprovaEntregaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (val tela = telaAtual) {
                        // Tela inicial
                        is NavScreen.Home -> {
                            HomeScreen(
                                entregas = entregas,
                                onNovaEntregaClick = { telaAtual = NavScreen.NewDelivery },
                                onEntregaClick = { entrega ->
                                    entregaSelecionada = entrega
                                    telaAtual = NavScreen.Details(entrega)
                                }
                            )
                        }

                        // Tela de adicionar entrega
                        is NavScreen.NewDelivery -> {
                            NewDeliveryScreen(
                                onBackClick = {
                                    telaAtual = NavScreen.Home
                                },
                                onSaveClick = { codigo, dest, obs, lat, lng, foto ->
                                    val novaEntrega = Delivery(
                                        codigoPedido = codigo, destinatario = dest,
                                        observacao = obs, latitude = lat,
                                        longitude = lng, photoPath = foto
                                    )

                                    scope.launch {
                                        dao.insertDelivery(novaEntrega)
                                        telaAtual = NavScreen.Home
                                    }
                                }
                            )
                        }

                        // Tela de detalhes da entrega
                        is NavScreen.Details -> {
                            DeliveryDetailScreen(
                                delivery = tela.delivery,
                                onBackClick = {
                                    telaAtual = NavScreen.Home
                                    entregaSelecionada = null
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}