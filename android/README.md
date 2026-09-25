# Placar de Tranca — Android

Aplicativo Android em Kotlin e Jetpack Compose para marcar os pontos de uma
partida de Tranca entre duas duplas. Recria as mesmas funcionalidades da
versão iOS, na pasta [`ios/`](../ios/).

## Funcionalidades

- nome de cada dupla, com confirmação ao alterar um nome já definido;
- pontuação positiva ou negativa por rodada, aceitando só números;
- painel com o total de cada dupla e indicação de quem está liderando;
- lista das rodadas, com opção para apagar uma rodada específica;
- botão para limpar o placar e opção de nova partida, ambos mantendo os nomes.

## Instalar o APK

O executável pronto fica em [`dist/Tranca-1.0.apk`](dist/Tranca-1.0.apk).

1. Copie o arquivo para o celular (ou baixe pelo GitHub direto no aparelho).
2. Abra o arquivo e permita a instalação de apps desta fonte quando o Android
   pedir.
3. Toque em **Instalar**.

O APK é assinado com a chave de debug do Android Studio, o que serve para
instalar manualmente. Para publicar na Play Store é preciso uma chave própria.

Para gerar de novo:

```sh
./gradlew assembleRelease
cp app/build/outputs/apk/release/app-release.apk dist/Tranca-1.0.apk
```

## Simulação da tela

Abra [`../preview/index.html`](../preview/index.html) no navegador para usar o
placar sem o Android Studio, com o mesmo visual e comportamento do app.

## Como executar

1. Abra este projeto no Android Studio.
2. Aguarde a sincronização do Gradle.
3. Execute o módulo `app` em um aparelho ou emulador com Android 8.0 ou
   superior.

## Testes

```sh
./gradlew test
```
