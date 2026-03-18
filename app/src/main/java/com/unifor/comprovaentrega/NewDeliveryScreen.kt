package com.unifor.comprovaentrega

import android.Manifest
import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDeliveryScreen(
    onBackClick: () -> Unit = {},
    onSaveClick: (
        codigoPedido: String,
        destinatario: String,
        observacao: String,
        lat: Double?,
        lng: Double?,
        foto: String?
    ) -> Unit = { _, _, _, _, _, _ -> }
) {
    val context = LocalContext.current

    // Estados dos campos de texto
    val codigoPedido = remember { mutableStateOf("") }
    val destinatario = remember { mutableStateOf("") }
    val observacao = remember { mutableStateOf("") }

    // Estados de erro para validação
    var codigoPedidoError by remember { mutableStateOf(false) }
    var destinatarioError by remember { mutableStateOf(false) }

    // Estados de Localização
    var locationCaptured by remember { mutableStateOf(false) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    // Estados da Foto
    var photoPathCaptured by remember { mutableStateOf<String?>(null) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para capturar a foto
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoPathCaptured = tempPhotoUri?.toString()
        }
    }

    // Launcher para Localização
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                        locationCaptured = true
                        Toast.makeText(context, "Localização capturada", Toast.LENGTH_SHORT).show()
                    } else {
                        // Tenta captura forçada se a última localização for nula
                        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                            .addOnSuccessListener { newLocation ->
                                if (newLocation != null) {
                                    latitude = newLocation.latitude
                                    longitude = newLocation.longitude
                                    locationCaptured = true
                                } else {
                                    Toast.makeText(context, "Sinal de GPS não encontrado. Verifique o emulador.", Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                }
            } catch (e: SecurityException) {
                Toast.makeText(context, "Permissão negada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Função para abrir câmera
    fun launchCamera() {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val photoFile = File.createTempFile("IMG_${timeStamp}_", ".jpg", context.cacheDir)
            val authority = "com.unifor.comprovaentrega.fileprovider"
            val photoUri = FileProvider.getUriForFile(context, authority, photoFile)
            tempPhotoUri = photoUri
            cameraLauncher.launch(photoUri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Nova Entrega") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
            Text(
                text = "Registrar comprovante",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Card do Formulário
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Dados da entrega",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = codigoPedido.value,
                        onValueChange = { codigoPedido.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Código do pedido") },
                        isError = codigoPedidoError,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = destinatario.value,
                        onValueChange = { destinatario.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Destinatário") },
                        isError = destinatarioError,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = observacao.value,
                        onValueChange = { observacao.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Observações") },
                        minLines = 4
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Card de Ações
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Comprovação",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    FilledTonalButton(
                        onClick = { launchCamera() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = if (photoPathCaptured != null) "Foto capturada" else "Capturar foto")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    FilledTonalButton(
                        onClick = { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = if (locationCaptured) "Localização capturada" else "Capturar localização")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (photoPathCaptured != null) "Foto: OK" else "Foto: Pendente",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (photoPathCaptured != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = if (locationCaptured) "Localização: OK" else "Localização: Pendente",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (locationCaptured) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val codigo = codigoPedido.value.trim()
                    val destValue = destinatario.value.trim()

                    codigoPedidoError = codigo.isBlank()
                    destinatarioError = destValue.isBlank()

                    if (!codigoPedidoError && !destinatarioError) {
                        onSaveClick(
                            codigo,
                            destValue,
                            observacao.value.trim(),
                            latitude,
                            longitude,
                            photoPathCaptured
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Salvar entrega")
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Cancelar")
            }
        }
    }
}