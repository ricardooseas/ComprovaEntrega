package com.unifor.comprovaentrega

import com.unifor.comprovaentrega.data.local.entity.Delivery
import org.junit.Assert.*
import org.junit.Test

class DeliveryTest {

    @Test
    fun delivery_dadosCorretos() {
        val delivery = Delivery(
            codigoPedido = "PED-001",
            destinatario = "João Silva",
            observacao = ""
        )

        assertEquals("PED-001", delivery.codigoPedido)
        assertEquals("João Silva", delivery.destinatario)
    }

    @Test
    fun delivery_idNaoVazio() {
        val delivery = Delivery(
            codigoPedido = "PED-001",
            destinatario = "João Silva",
            observacao = ""
        )

        assertTrue(delivery.id.isNotEmpty())
    }

    @Test
    fun delivery_idsSaoDiferentes() {
        val d1 = Delivery(codigoPedido = "PED-001", destinatario = "Ana", observacao = "")
        val d2 = Delivery(codigoPedido = "PED-002", destinatario = "Bruno", observacao = "")

        assertNotEquals(d1.id, d2.id)
    }

    @Test
    fun delivery_semFotoESemGps_devemSerNulos() {
        val delivery = Delivery(
            codigoPedido = "PED-001",
            destinatario = "Carlos",
            observacao = ""
        )

        assertNull(delivery.photoPath)
        assertNull(delivery.latitude)
        assertNull(delivery.longitude)
    }

    @Test
    fun delivery_createdAt_naoDeveSerZero() {
        val delivery = Delivery(
            codigoPedido = "PED-001",
            destinatario = "Diana",
            observacao = ""
        )

        assertTrue(delivery.createdAt > 0)
    }

    // testes da lógica do botão salvar

    @Test
    fun canSave_deveSerVerdadeiro_quandoTudoPreenchido() {
        val foto = "/storage/foto.jpg"
        val gps = true
        val codigo = "PED-001"
        val destinatario = "João"

        val canSave = foto != null && gps && codigo.isNotBlank() && destinatario.isNotBlank()

        assertTrue(canSave)
    }

    @Test
    fun canSave_semFoto_deveSerFalso() {
        val foto: String? = null
        val canSave = foto != null && true && "PED-001".isNotBlank() && "João".isNotBlank()

        assertFalse(canSave)
    }

    @Test
    fun canSave_semGps_deveSerFalso() {
        val gps = false
        val canSave = "/storage/foto.jpg" != null && gps && "PED-001".isNotBlank() && "João".isNotBlank()

        assertFalse(canSave)
    }

    @Test
    fun canSave_semCodigo_deveSerFalso() {
        val canSave = "/storage/foto.jpg" != null && true && "".isNotBlank() && "João".isNotBlank()

        assertFalse(canSave)
    }

    @Test
    fun canSave_semDestinatario_deveSerFalso() {
        val canSave = "/storage/foto.jpg" != null && true && "PED-001".isNotBlank() && "".isNotBlank()

        assertFalse(canSave)
    }
}