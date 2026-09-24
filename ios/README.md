# Placar de Tranca

Aplicativo simples para iPhone, feito em SwiftUI, para marcar os pontos de uma
partida de Tranca entre duas duplas.

## Funcionalidades

- nomes dos quatro jogadores;
- pontuação positiva ou negativa por rodada;
- soma automática dos pontos de cada dupla;
- histórico das rodadas e opção para desfazer o último lançamento;
- resultado final com indicação da dupla vencedora;
- nova partida sem apagar os nomes.

## Como executar

1. Abra `TrancaScore.xcodeproj` no Xcode 16 ou superior.
2. Selecione um simulador de iPhone com iOS 17 ou superior.
3. Pressione **Run** (`⌘R`).

Para instalar em um iPhone físico, selecione sua equipe de desenvolvimento em
**Signing & Capabilities**.

## Testes

A lógica de pontuação pode ser testada em qualquer ambiente com Swift 5.9:

```sh
swift test
```
