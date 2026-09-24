# Placar de Tranca

Este repositório contém duas aplicações independentes para marcar o placar de
partidas de Tranca entre duas duplas. Cada plataforma fica em sua própria pasta.

## Aplicações

| Pasta | Plataforma | Tecnologia | Requisitos |
| --- | --- | --- | --- |
| [`ios/`](ios/) | iPhone | Swift e SwiftUI | Xcode 16 e iOS 17+ |
| [`android/`](android/) | Android | Kotlin e Jetpack Compose | Android Studio e Android 8.0+ |

As duas versões permitem:

- informar os nomes dos quatro jogadores;
- adicionar pontuações positivas ou negativas por rodada;
- acompanhar a soma automática e o histórico;
- desfazer o último lançamento;
- visualizar o resultado final;
- iniciar uma nova partida mantendo os nomes.

## Como executar

### iOS

Abra [`ios/TrancaScore.xcodeproj`](ios/TrancaScore.xcodeproj) no Xcode 16 ou
superior e execute em um simulador de iPhone com iOS 17 ou superior. Detalhes
em [`ios/README.md`](ios/README.md).

### Android

Abra a pasta [`android/`](android/) no Android Studio e execute o módulo `app`
em um aparelho ou emulador com Android 8.0 ou superior. Detalhes em
[`android/README.md`](android/README.md).

Para usar o placar no navegador, sem o Android Studio, abra
[`android/preview/index.html`](android/preview/index.html).
