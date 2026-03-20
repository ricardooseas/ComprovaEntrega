package com.unifor.comprovaentrega.ui.details

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.unifor.comprovaentrega.data.local.AppDatabase
import com.unifor.comprovaentrega.data.local.entity.Delivery
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDetailScreen(
    delivery: Delivery,
    onBackClick: () -> Unit
) {

    var mostrarDialogDeletar by remember { mutableStateOf(false) }
    var inputSenha by remember { mutableStateOf("") }
    val senhaCorreta = "1234"
    val scope = rememberCoroutineScope()

    val dao = AppDatabase.getDatabase(LocalContext.current).deliveryDao()
    val context = LocalContext.current

    if (mostrarDialogDeletar) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogDeletar = false
                inputSenha = ""
            },
            title = { Text("Confirmar Exclusão") },
            text = {
                Column {
                    Text("Esta ação não pode ser desfeita. Digite a senha para confirmar (Administrador):")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputSenha,
                        onValueChange = { inputSenha = it },
                        label = { Text("Senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputSenha == senhaCorreta) {
                            scope.launch {
                                dao.deleteDelivery(delivery)
                                mostrarDialogDeletar = false
                                Toast.makeText(context, "Entrega excluída!", Toast.LENGTH_SHORT)
                                    .show()
                                onBackClick()
                            }
                        } else {
                            Toast.makeText(context, "Senha incorreta!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Deletar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogDeletar = false
                    inputSenha = ""
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes da Entrega") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarDialogDeletar = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Deletar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.Top
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Código: ${delivery.codigoPedido}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text("Criado em: ${formatTimestamp(delivery.createdAt)}")
                    Text("Destinatário: ${delivery.destinatario}")
                    if (delivery.observacao.isNotEmpty()) {
                        Text("Observação: ${delivery.observacao}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Foto de Comprovação:", style = MaterialTheme.typography.titleMedium)
                    AsyncImage(
                        model = delivery.photoPath,
                        contentDescription = "Foto da entrega",
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Localização da Entrega:", style = MaterialTheme.typography.titleMedium)
                    if (delivery.latitude != null && delivery.longitude != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(0.dp))
                        ) {
                            AndroidView(
                                factory = { ctx ->
                                    MapView(ctx).apply {
                                        setMultiTouchControls(false)
                                        controller.setZoom(16.0)
                                        val geoPoint =
                                            GeoPoint(delivery.latitude, delivery.longitude)
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botão para Compartilhar
                Button(
                    onClick = {
                        shareDelivery(
                            context,
                            delivery
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Compartilhar")
                }

                // Botão para Copiar Link
                OutlinedButton(
                    onClick = {
                        val uri =
                            "https://www.google.com/maps/search/?api=1&query=${delivery.latitude},${delivery.longitude}"
                        copyToClipboard(context, uri)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Copiar Link")
                }
            }
        }
    }
}

fun shareDelivery(context: Context, delivery: Delivery) {
    val mapLink =
        "https://www.google.com/maps/search/?api=1&query=${delivery.latitude},${delivery.longitude}"
    val message = "Entrega realizada para: ${delivery.destinatario}\n" +
            "Código: ${delivery.codigoPedido}\n" +
            "Localização: $mapLink"

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_TEXT, message)

        delivery.photoPath?.let { path ->
            val imageFile = File(path)
            if (imageFile.exists()) {
                val imageUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    imageFile
                )
                putExtra(Intent.EXTRA_STREAM, imageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
    }

    context.startActivity(Intent.createChooser(shareIntent, "Compartilhar Comprovação"))
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Localização Entrega", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Link copiado!", Toast.LENGTH_SHORT).show()
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}