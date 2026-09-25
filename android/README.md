# Placar de Tranca — Android

Aplicativo Android em Kotlin e Jetpack Compose para marcar os pontos de uma
partida de Tranca entre duas duplas. Recria as mesmas funcionalidades da
versão iOS, na pasta [`ios/`](../ios/).

## Funcionalidades

- nome de cada dupla, com confirmação ao alterar um nome já definido;
- pontuação positiva ou negativa por rodada, aceitando só números;
- soma automática dos pontos de cada dupla na primeira linha do histórico;
- opção para apagar uma rodada específica, com confirmação;
- botão para limpar o placar e opção de nova partida, ambos mantendo os nomes.

## Simulação da tela

Abra [`../preview/index.html`](../preview/index.html) no navegador para usar o
placar sem o Android Studio.

## Como executar

1. Abra este projeto no Android Studio.
2. Aguarde a sincronização do Gradle.
3. Execute o módulo `app` em um aparelho ou emulador com Android 8.0 ou
   superior.

## Testes

```sh
./gradlew test
```
