# Nexo App 

O **Nexo App** é o cliente mobile oficial da plataforma **Nexo Art**, desenvolvido nativamente para o ecossistema Android. O aplicativo oferece uma experiência fluida e dinâmica para artistas digitais, permitindo a navegação por feeds de arte, busca local de portfólios e publicação em tempo real direto da galeria do dispositivo.

##  Tecnologias e Bibliotecas

- **Linguagem:** Kotlin
- **Arquitetura:** Baseada em Components, Activities, Fragments e Listas Dinâmicas.
- **Comunicação em Rede:** Retrofit 2 + OkHttp 3 (Consumo de API REST assíncrona).
- **Processamento e Cache de Imagens:** Glide (Renderização inteligente de URLs da internet).
- **Layout UI:** XML com componentes modernos de Material Design (CardView, RecyclerView, FloatingActionButton).

##  Funcionalidades Implementadas (MVP)

- **Fluxo de Autenticação Real:** Tela de login integrada ao backend via Spring Security com armazenamento e persistência de Token JWT para requisições seguras.
- **Feed Dinâmico (Home):** RecyclerView de alta performance que consome o endpoint `/posts` da API, mapeando objetos JSON do backend para componentes visuais do Android.
- **Upload de Arquivos Multipart:** Fluxo de publicação que consome a galeria nativa do Android, converte URIs em arquivos temporários seguros e faz o upload assíncrono via formulário HTTP Multipart para o servidor.
- **Busca e Filtragem Local:** Barra de pequisa interativa com filtragem dinâmica em tempo real utilizando um `StaggeredGridLayoutManager` para simular uma disposição estilo mosaico de portfólio de arte.

##  Permissões Requeridas

O aplicativo respeita as diretrizes modernas de segurança do Android (incluindo Android 13+), exigindo permissões estritas para acesso a recursos do sistema:
- `android.permission.INTERNET` - Para comunicação com as rotas de API.
- `android.permission.READ_EXTERNAL_STORAGE` / `READ_MEDIA_IMAGES` - Para carregar imagens da galeria de forma segura durante a postagem.

##  Como Executar o Aplicativo

1. Abra o projeto no **Android Studio**.
2. Certifique-se de atualizar a URL base do servidor no seu arquivo `RetrofitClient` para apontar para o IP correto da sua máquina de desenvolvimento (ou `10.0.2.2` caso esteja usando o emulador padrão do Android Studio).
3. Sincronize as dependências do Gradle clicando em `Sync Project with Gradle Files`.
4. Conecte um dispositivo físico com depuração USB ativa ou utilize um Emulador Android (API 33+ recomendada).
5. Clique no botão **Run (Play)** no topo do Android Studio.
