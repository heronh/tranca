# Placar de Tranca

Este repositório contém duas aplicações independentes para marcar o placar de
partidas de Tranca entre duas duplas. Cada plataforma está em seu próprio
branch.

## Aplicações

| Branch | Plataforma | Tecnologia | Requisitos |
| --- | --- | --- | --- |
| [`tranca-ios`](../../tree/tranca-ios) | iPhone | Swift e SwiftUI | Xcode 16 e iOS 17+ |
| [`tranca-android`](../../tree/tranca-android) | Android | Kotlin e Jetpack Compose | Android Studio e Android 8.0+ |

As duas versões permitem:

- informar os nomes dos quatro jogadores;
- adicionar pontuações positivas ou negativas por rodada;
- acompanhar a soma automática e o histórico;
- desfazer o último lançamento;
- visualizar o resultado final;
- iniciar uma nova partida mantendo os nomes.

## Acessar uma versão

```sh
git switch tranca-ios
```

ou:

```sh
git switch tranca-android
```

O branch `main` funciona como índice do projeto; o código de cada aplicação
permanece isolado em seu respectivo branch.
