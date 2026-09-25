# Placar de Tranca

Este repositório contém duas aplicações independentes para marcar o placar de
partidas de Tranca entre duas duplas. Cada plataforma fica em sua própria pasta,
e as duas têm o mesmo visual e comportamento.

## Aplicações

| Pasta | Plataforma | Tecnologia | Requisitos |
| --- | --- | --- | --- |
| [`ios/`](ios/) | iPhone | Swift e SwiftUI | Xcode 16 e iOS 17+ |
| [`android/`](android/) | Android | Kotlin e Jetpack Compose | Android Studio e Android 8.0+ |

As duas versões permitem:

- informar o nome de cada dupla, com confirmação ao alterar um nome já definido;
- adicionar pontuações positivas ou negativas por rodada (o campo aceita só números);
- acompanhar o total de cada dupla e ver quem está liderando;
- apagar uma rodada específica, com confirmação;
- limpar o placar ou iniciar uma nova partida mantendo os nomes.

## Como executar

### iPhone

Siga o passo a passo em [`IPHONE.md`](IPHONE.md). A versão web vai para a Tela
de Início sem precisar de Xcode; o app nativo roda pelo Xcode, com detalhes em
[`ios/README.md`](ios/README.md).

### Android

Instale direto no celular o APK pronto em
[`android/dist/Tranca-1.0.apk`](android/dist/Tranca-1.0.apk), ou abra a pasta
[`android/`](android/) no Android Studio e execute o módulo `app`. Detalhes em
[`android/README.md`](android/README.md).

## Simulação

Abra [`preview/index.html`](preview/index.html) no navegador para usar o placar
sem Xcode ou Android Studio. A simulação tem o mesmo visual e comportamento dos
aplicativos, guarda o jogo no navegador e, publicada no GitHub Pages, fica em
https://heronh.github.io/tranca/preview/.
