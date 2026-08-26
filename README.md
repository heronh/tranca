# Placar de Tranca — Android

Aplicativo Android em Kotlin e Jetpack Compose para marcar os pontos de uma
partida de Tranca entre duas duplas.

## Funcionalidades

- nomes dos quatro jogadores;
- pontuação positiva ou negativa por rodada;
- soma automática dos pontos de cada dupla;
- histórico das rodadas e opção de desfazer o último lançamento;
- resultado final com indicação da dupla vencedora;
- nova partida sem apagar os nomes.

## Simulação da tela

Abra `preview/index.html` no navegador para usar o placar sem o Android
Studio. A simulação replica nomes, soma das rodadas, histórico, desfazer,
resultado final e nova partida.

Para servir localmente:

```sh
python3 -m http.server 8080 --directory preview
```

Depois acesse `http://127.0.0.1:8080/`.

## Como executar

1. Abra este projeto no Android Studio.
2. Aguarde a sincronização do Gradle.
3. Execute o módulo `app` em um aparelho ou emulador com Android 8.0 ou
   superior.

## Testes

```sh
./gradlew test
```
