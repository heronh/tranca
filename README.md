# Placar de Tranca

Este repositório contém duas aplicações independentes para marcar o placar de
partidas de Tranca entre duas duplas. Cada plataforma fica em sua própria pasta.

## Aplicações

| Pasta | Plataforma | Tecnologia | Requisitos |
| --- | --- | --- | --- |
| [`ios/`](ios/) | iPhone | Swift e SwiftUI | Xcode 16 e iOS 17+ |
| [`android/`](android/) | Android | Kotlin e Jetpack Compose | Android Studio e Android 8.0+ |

As duas versões permitem:

- informar o nome de cada dupla, com confirmação ao alterar um nome já definido;
- adicionar pontuações positivas ou negativas por rodada (o campo aceita só números);
- acompanhar a soma de cada dupla na primeira linha do histórico;
- apagar uma rodada específica, com confirmação;
- limpar o placar ou iniciar uma nova partida mantendo os nomes.

## Como executar

### iOS

Abra [`ios/TrancaScore.xcodeproj`](ios/TrancaScore.xcodeproj) no Xcode 16 ou
superior e execute em um simulador de iPhone com iOS 17 ou superior. Detalhes
em [`ios/README.md`](ios/README.md).

### Android

Abra a pasta [`android/`](android/) no Android Studio e execute o módulo `app`
em um aparelho ou emulador com Android 8.0 ou superior. Detalhes em
[`android/README.md`](android/README.md).

## Simulação

Abra [`preview/index.html`](preview/index.html) no navegador para usar a mesma
tela do placar, sem Xcode ou Android Studio. A simulação tem o mesmo
comportamento dos dois aplicativos.
