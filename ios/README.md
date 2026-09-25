# Placar de Tranca — iPhone

Aplicativo para iPhone, feito em SwiftUI, para marcar os pontos de uma partida
de Tranca entre duas duplas. Tem o mesmo visual e comportamento da versão
Android, na pasta [`android/`](../android/).

## Funcionalidades

- nome de cada dupla, com confirmação ao alterar um nome já definido;
- pontuação positiva ou negativa por rodada, aceitando só números;
- painel com o total de cada dupla e indicação de quem está liderando;
- lista das rodadas, com opção para apagar uma rodada específica;
- botão para limpar o placar e opção de nova partida, ambos mantendo os nomes.

## Passo a passo para rodar no iPhone

### 1. Preparar o Mac

1. Instale o **Xcode 16 ou superior** pela App Store (é grande, leva um tempo).
2. Abra o Xcode uma vez, aceite a licença e deixe ele instalar os componentes
   adicionais.
3. Em **Xcode › Settings › Components**, confirme que a plataforma **iOS** está
   instalada (é ela que traz os simuladores).
4. No Terminal, aponte as ferramentas de linha de comando para o Xcode:

   ```sh
   sudo xcode-select -s /Applications/Xcode.app
   ```

### 2. Abrir o projeto

1. Baixe o repositório (ou atualize com `git pull`).
2. Dê dois cliques em `ios/TrancaScore.xcodeproj`, ou rode:

   ```sh
   open ios/TrancaScore.xcodeproj
   ```

### 3. Rodar no simulador (sem iPhone)

1. Na barra superior do Xcode, ao lado do nome **TrancaScore**, escolha um
   simulador, por exemplo **iPhone 16**.
2. Pressione **Run** (`⌘R`).
3. O simulador abre com o app **Placar de Tranca**.

### 4. Rodar no seu iPhone

1. Em **Xcode › Settings › Accounts**, toque em **+** e entre com seu Apple ID.
   Uma conta gratuita funciona.
2. No navegador de arquivos do Xcode, selecione o projeto **TrancaScore**, o
   target **TrancaScore** e a aba **Signing & Capabilities**.
3. Deixe **Automatically manage signing** marcado e escolha seu nome em
   **Team**.
4. Se aparecer erro dizendo que o identificador já está em uso, troque o
   **Bundle Identifier** `com.trancascore.app` por algo único, como
   `com.seunome.tranca`.
5. Conecte o iPhone ao Mac pelo cabo, desbloqueie e toque em **Confiar** neste
   computador.
6. No iPhone, ative o **Modo de Desenvolvedor** em **Ajustes › Privacidade e
   Segurança › Modo de Desenvolvedor**. O iPhone reinicia e pede confirmação.
7. No Xcode, escolha o seu iPhone na lista de destinos e pressione **Run**
   (`⌘R`).
8. Na primeira vez, o iPhone bloqueia o app. Libere em **Ajustes › Geral › VPN e
   Gerenciamento de Dispositivos**, toque no seu Apple ID e em **Confiar**.
9. Abra o **Placar de Tranca** no iPhone.

Com conta gratuita, o app instalado vale por 7 dias; depois basta repetir o
passo 7 com o iPhone conectado. Com o Apple Developer Program (pago), vale por
um ano e pode ser distribuído pelo TestFlight.

### Problemas comuns

- **"No such module" ou erro de SDK**: confirme o passo 1.4 e que o Xcode é 16
  ou superior.
- **iPhone não aparece na lista**: desbloqueie o aparelho, confirme a
  confiança no computador e verifique se o Modo de Desenvolvedor está ativo.
- **"Untrusted Developer"**: faça o passo 4.8.

## Testes

A lógica de pontuação pode ser testada com o Xcode instalado:

```sh
cd ios
swift test
```
