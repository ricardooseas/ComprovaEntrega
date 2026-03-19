package com.unifor.comprovaentrega.ui.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.unifor.comprovaentrega.data.local.entity.Delivery
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    entregas: List<Delivery>,
    onNovaEntregaClick: () -> Unit = {},
    onEntregaClick: (Delivery) -> Unit = {}
) {
    var mostrarDialogSobre by remember { mutableStateOf(false) }

    if (mostrarDialogSobre) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogSobre = false
            },
            title = { Text("Sobre") },
            text = {
                Column {
                    Text("N700 Desenvolvimento para Plataformas Móveis", style = MaterialTheme.typography.headlineSmall)
                    Text("Desenvolvido pelo grupo 20:")
                    Text("# 2425059 - Arimateia Barbosa")
                    Text("# 2425262 - Ricardo Oseas")
                    Text("# 2416703 - Alberto Luiz")
                    Text("# 2631611 - Pedro Gabriel")
                }
            },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogSobre = false },
                ) {
                    Text("Ok")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Comprova Entrega") },
                actions = {
                    IconButton(onClick = { mostrarDialogSobre = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Sobre"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNovaEntregaClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(text = "+", style = MaterialTheme.typography.headlineSmall)
            }
        }
    ) { padding ->
        if (entregas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhuma entrega registrada",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = entregas,
                    key = { it.id }
                ) { entrega ->
                    Box(
                        modifier = Modifier.animateItem()
                    ) {
                        DeliveryCard(
                            entrega = entrega,
                            onClick = { onEntregaClick(entrega) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeliveryCard(
    entrega: Delivery,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Configuration.getInstance().userAgentValue = context.packageName

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pedido: ${entrega.codigoPedido}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatTimestamp(entrega.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "Destinatário: ${entrega.destinatario}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (entrega.observacao.isNotEmpty()) {
                Text(
                    text = "Observação: ${entrega.observacao}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // --- MAPA GRATUITO INTEGRADO ---
            if (entrega.latitude != null && entrega.longitude != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AndroidView(
                        factory = { ctx ->
                            MapView(ctx).apply {
                                setMultiTouchControls(false) // Desativa gestos para não travar o scroll da lista
                                controller.setZoom(16.0)
                                val geoPoint = GeoPoint(entrega.latitude, entrega.longitude)
                                controller.setCenter(geoPoint)

                                val marker = Marker(this)
                                marker.position = geoPoint
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                marker.title = "Local da Entrega"
                                overlays.add(marker)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}