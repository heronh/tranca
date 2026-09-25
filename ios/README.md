# Placar de Tranca

Aplicativo simples para iPhone, feito em SwiftUI, para marcar os pontos de uma
partida de Tranca entre duas duplas.

## Funcionalidades

- nome de cada dupla, com confirmação ao alterar um nome já definido;
- pontuação positiva ou negativa por rodada, aceitando só números;
- soma automática dos pontos de cada dupla na primeira linha do histórico;
- opção para apagar uma rodada específica, com confirmação;
- botão para limpar o placar e opção de nova partida, ambos mantendo os nomes.

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
