# 📦 ComprovaEntrega

> **Aviso:** Este projeto foi desenvolvido para fins educacionais, visando a aplicação prática de conceitos de desenvolvimento Android nativo.

---

## 📱 Sobre o Projeto

O **ComprovaEntrega** é um aplicativo móvel Android focado em resolver um desafio logístico real: a **comprovação confiável de entregas**. O aplicativo permite que entregadores registrem a conclusão de uma entrega capturando a assinatura/foto do pacote e a localização exata do momento da entrega, garantindo segurança e transparência tanto para o remetente quanto para o destinatário.

---

## 🛠️ Tecnologias e Recursos Utilizados

O desenvolvimento deste MVP (Minimum Viable Product) aplica boas práticas de engenharia de software e explora os recursos nativos do dispositivo Android:

### Core e UI
- **Android Studio** como IDE principal.
- **Kotlin** como linguagem de programação.
- **Jetpack Compose** para a construção da interface de forma declarativa e reativa.
- **Material Design 3** para componentes visuais, tipografia e cores consistentes.

### Sensores e Recursos Nativos
- 📸 **Câmera:** Utilizada nativamente via `FileProvider` e `ActivityResultContracts.TakePicture()` para registrar a foto do pacote ou do canhoto assinado no momento da entrega.
- 📍 **GPS (Localização):** Utilizado via `FusedLocationProviderClient` do Google Play Services para capturar as coordenadas exatas (latitude e longitude) da entrega, garantindo a autenticidade do registro.
- 📂 **Armazenamento Local:** Manipulação de arquivos temporários para salvar as fotos tiradas pelo dispositivo antes de consolidar a entrega.
- 🗺️ **Mapas (OpenStreetMap):** Integração com `OSMDroid` para exibir a localização da entrega visualmente sem necessidade de chaves de API pagas.

---

## 🚀 Funcionalidades (Scope)

As funcionalidades principais foram levantadas baseadas em requisitos funcionais e não funcionais para o MVP:

- [x] Cadastro de nova entrega (Código do pedido, Destinatário, Observações).
- [x] Captura de foto via câmera nativa do dispositivo.
- [x] Captura de geolocalização exata no momento da entrega via GPS do Android.
- [x] Visualização em mapa da coordenada registrada.
- [x] Interface moderna com Jetpack Compose e Material Design.
- [x] Persistência de dados com SQLite

---

## 🎨 Prototipação e Design

A etapa inicial do projeto contou com o levantamento de requisitos e a prototipação de wireframes utilizando **Figma**. O objetivo foi mapear o fluxo do usuário (UX) antes da implementação em código, assegurando que o uso em campo por entregadores seja ágil, exigindo o mínimo de cliques possível.

---

## ⚙️ Como Executar o Projeto

1. Certifique-se de ter o **Android Studio** instalado (versões recentes recomendadas).
2. Clone este repositório em sua máquina:
   ```bash
   git clone https://github.com/ricardooseas/ComprovaEntrega.git
   ```
3. Abra a pasta do projeto clonada no Android Studio.
4. Aguarde o **Gradle** sincronizar todas as dependências (`androidx.compose`, `play-services-location`, `osmdroid`, etc).
5. Conecte um dispositivo físico via cabo USB/Wi-Fi (recomendado para testar câmera e GPS real) ou inicie um Emulador.
6. Clique em **Run 'app'** (`Shift + F10`).

> **Nota sobre o Emulador:** Para testar a funcionalidade de GPS no emulador Android, vá nos três pontinhos da barra lateral do emulador (Extended controls) > Location > defina uma rota ou ponto específico e clique em "Set Location".

---
