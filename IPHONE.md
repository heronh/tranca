# Como usar o placar no iPhone

Há dois caminhos. A versão web funciona hoje, sem Xcode, e fica na Tela de
Início como um app. O app nativo exige o Xcode instalado no Mac.

## Opção 1 — versão web na Tela de Início (sem Xcode)

### Publicar (uma única vez)

1. Abra [github.com/heronh/tranca](https://github.com/heronh/tranca) e entre na
   sua conta.
2. Vá em **Settings › Pages**.
3. Em **Build and deployment › Source**, escolha **Deploy from a branch**.
4. Em **Branch**, escolha **main** e a pasta **/ (root)**, e toque em **Save**.
5. Aguarde cerca de 1 minuto. O endereço do placar passa a ser:

   **https://heronh.github.io/tranca/preview/**

Cada `git push` na `main` atualiza o placar publicado em cerca de 1 minuto.

### Instalar no iPhone

1. No iPhone, abra o endereço acima no **Safari**.
2. Toque no botão **Compartilhar** (quadrado com a seta para cima).
3. Role e toque em **Adicionar à Tela de Início**.
4. Confira o nome **Tranca** e toque em **Adicionar**.
5. Abra pelo ícone verde com o ♣. O placar abre em tela cheia, sem a barra do
   Safari.

O jogo fica salvo no próprio iPhone: dá para fechar o app e continuar depois.
**Limpar placar** e **Nova partida** apagam as rodadas e mantêm os nomes.

### Testar antes de publicar (mesma Wi-Fi)

1. No Mac, na pasta do projeto, rode:

   ```sh
   python3 -m http.server 8765 --directory preview
   ```

2. Descubra o IP do Mac em **Ajustes do Sistema › Wi-Fi › Detalhes** (algo como
   `192.168.0.10`).
3. Com o iPhone na mesma rede Wi-Fi, abra no Safari `http://IP-DO-MAC:8765/`.
4. Se quiser, adicione à Tela de Início como acima. Esse atalho só funciona
   enquanto o Mac estiver ligado com o servidor rodando.

## Opção 2 — app nativo (com Xcode)

Resumo; o detalhamento está em [ios/README.md](ios/README.md).

1. Instale o **Xcode 16 ou superior** pela App Store, abra uma vez e rode
   `sudo xcode-select -s /Applications/Xcode.app`.
2. Abra `ios/TrancaScore.xcodeproj`.
3. Em **Xcode › Settings › Accounts**, entre com seu Apple ID (a conta gratuita
   serve).
4. No target **TrancaScore**, aba **Signing & Capabilities**, escolha seu
   **Team**. Se o identificador estiver em uso, troque o **Bundle Identifier**
   por algo como `com.seunome.tranca`.
5. Conecte o iPhone pelo cabo, desbloqueie e toque em **Confiar**.
6. No iPhone, ative **Ajustes › Privacidade e Segurança › Modo de
   Desenvolvedor** e confirme após reiniciar.
7. No Xcode, escolha o seu iPhone como destino e pressione **Run** (`⌘R`).
8. Na primeira vez, libere em **Ajustes › Geral › VPN e Gerenciamento de
   Dispositivos › seu Apple ID › Confiar**.

Com conta gratuita, o app instalado vale 7 dias; para renovar, repita o passo 7.
