package com.unifor.comprovaentrega.ui.newdelivery

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.LocationServices
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

    // Estados
    var absolutePhotoPath by remember { mutableStateOf<String?>(null) }
    val codigoPedido = remember { mutableStateOf("") }
    val destinatario = remember { mutableStateOf("") }
    val observacao = remember { mutableStateOf("") }

    // Estados de Localização
    var locationCaptured by remember { mutableStateOf(false) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var photoPathCaptured by remember { mutableStateOf<String?>(null) }
    var compressionInfo by remember { mutableStateOf<String?>(null) }

    // ─── Camera Launchers ───
    fun compressImage(originalPath: String): String {
        val compressedFile = File(originalPath.replace(".jpg", "_compressed.jpg"))
        try {
            val bitmap = android.graphics.BitmapFactory.decodeFile(originalPath)
            val out = java.io.FileOutputStream(compressedFile)
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, out)
            out.flush()
            out.close()
            val originalSize = File(originalPath).length() / 1024
            val compressedSize = compressedFile.length() / 1024
            compressionInfo = "($originalSize KB -> $compressedSize KB)"
            android.util.Log.d(
                "COMPRESSAO",
                "Original: ${originalSize}KB -> Comprimido: ${compressedSize}KB"
            )
        } catch (e: Exception) {
            android.util.Log.e("COMPRESSAO", "Erro ao comprimir: ${e.message}")
            return originalPath
        }
        return compressedFile.absolutePath
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            absolutePhotoPath?.let { path ->
                photoPathCaptured = compressImage(path)
            }
        }
    }

    fun launchCamera() {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir: File? = context.getExternalFilesDir(null)
            val photoFile = File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir)

            absolutePhotoPath = photoFile.absolutePath
            val authority = "com.unifor.comprovaentrega.fileprovider"
            val photoUri = FileProvider.getUriForFile(context, authority, photoFile)

            cameraLauncher.launch(photoUri)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erro ao criar arquivo", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(
                context,
                "Permissão de câmera necessária para a foto",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // ─── Location Launchers ───
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
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
                        fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            null
                        )
                            .addOnSuccessListener { newLocation ->
                                if (newLocation != null) {
                                    latitude = newLocation.latitude
                                    longitude = newLocation.longitude
                                    locationCaptured = true
                                }
                            }
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
                Toast.makeText(context, "Erro de permissão", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun checkCameraPermissionAndLaunch() {
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
            PackageManager.PERMISSION_GRANTED -> launchCamera()
            else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // ─── UI (resto permanece igual) ───
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Cadastrar Entrega") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
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
            Text(
                text = "Registrar comprovante",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = codigoPedido.value,
                        onValueChange = { codigoPedido.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Código do pedido") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = destinatario.value,
                        onValueChange = { destinatario.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Destinatário") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = observacao.value,
                        onValueChange = { observacao.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Observações") },
                        minLines = 3
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Comprovação",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    FilledTonalButton(
                        onClick = { checkCameraPermissionAndLaunch() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = if (photoPathCaptured != null)
                                    "Foto capturada com sucesso"
                                else
                                    "Capturar foto do comprovante"
                            }
                    ) {
                        Text(text = if (photoPathCaptured != null) "Foto capturada" else "Capturar foto")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    FilledTonalButton(
                        onClick = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = if (locationCaptured)
                                    "Localização capturada com sucesso"
                                else
                                    "Capturar localização GPS da entrega"
                            }
                    ) {
                        Text(text = if (locationCaptured) "Localização capturada" else "Capturar localização")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    StatusText(
                        label = "Foto",
                        isOk = photoPathCaptured != null,
                        extraInfo = compressionInfo
                    )
                    StatusText(label = "Localização", isOk = locationCaptured)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val canSave = photoPathCaptured != null && locationCaptured &&
                    codigoPedido.value.isNotBlank() && destinatario.value.isNotBlank()

            Button(
                onClick = {
                    onSaveClick(
                        codigoPedido.value.trim(),
                        destinatario.value.trim(),
                        observacao.value.trim(),
                        latitude,
                        longitude,
                        photoPathCaptured
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = canSave
            ) {
                Text(text = "Salvar entrega")
            }

            if (!canSave) {
                Text(
                    text = "* Foto, localização e dados básicos são obrigatórios.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .semantics {
                            contentDescription = "Campos obrigatórios pendentes: preencha todos os dados, capture a foto e a localização"
                        }
                )
            }
        }
    }
}

@Composable
fun StatusText(label: String, isOk: Boolean, extraInfo: String? = null) {
    Row {
        Text(
            text = if (isOk) "$label: OK" else "$label: Pendente",
            style = MaterialTheme.typography.bodySmall,
            color = if (isOk) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
        )
        if (isOk && extraInfo != null) {
            Text(
                text = " $extraInfo",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
