# nablarch-scoped-container


## 仕様

[JSR330](https://javax-inject.github.io/javax-inject/)へ準拠している(TCKをパスしている)。

標準で次のスコープをサポートしている。
カッコ内はそれぞれ対応するアノテーション。

- シングルトン(`@Singleton`)
- プロトタイプ(`@Prototype`)
- リクエストスコープ(`@RequestScoped`)
- セッションスコープ(`@SessionScoped`)

インジェクションを行うためのアノテーションは`@Inject`。

```java
@RequestScoped
public class HelloAction {

    @Inject
    private HelloService service;

    (省略)
}
```

インジェクションの方法は次の3つをサポートしている。

- コンストラクターインジェクション
- フィールドインジェクション
- メソッドインジェクション

限定子を使うことで同じ型で解決できるコンポーネントを複数扱うことができる。

```java
public interface Hello {
    (省略)
}

@Named("foo")
public class FooHello implements Hello {
    (省略)
}

@Named("bar")
public class BarHello implements Hello {
    (省略)
}

@Singleton
public class HelloService {

    @Inject
    @Named("foo")
    private Hello foo;

    @Inject
    @Named("bar")
    private Hello bar;

    (省略)
}
```

コンポーネントの型やスーパークラス、実装インターフェースで宣言されたフィールド・パラメーターにインジェクションできる。
これはつまり`Foo`インターフェースがあり、それの実装クラスとして`FooImpl`があった場合、`FooImpl`型のコンポーネントを`Foo`型のフィールド・パラメーターにインジェクションできるということ。

```java
public interface Foo {
}

@Singleton
public class FooImpl implements Foo {
}
```

```java
@Inject
private Foo foo; //FooImplをインジェクションできる
```

次のようなインターフェース、クラスがあったとする。

```java
public interface Foo {}

@Singleton
public class FooImpl implements Foo {}

public interface Bar extends Foo {}

@Singleton
public class BarImpl extends FooImpl implements Bar {}
```

```java
public interface Baz {}

@Named("xyz")
public class BazImpl {}
```

このときインジェクションがサポートされる型の関係性は次の通り。

| インジェクションされるフォールド・パラメーターの型 | コンポーネントの型 |
|----------------------------------------------------|--------------------|
| `Foo`                                              | `FooImpl`          |
| `FooImpl`                                          | `FooImpl`          |
| `Bar`                                              | `BarImpl`          |
| `BarImpl`                                          | `BarImpl`          |
| `Foo`                                              | `BarImpl`          |
| `FooImpl`                                          | `BarImpl`          |
| `Baz`                                              | `BazImpl`          |
| `BazImpl`                                          | `BazImpl`          |
| `@Named("xyz") Baz`                                | `BazImpl`          |
| `@Named("xyz") BazImpl`                            | `BazImpl`          |

初期化、破棄を行うライフサイクルメソッドをサポートしている。
それぞれ`@Init`、`@Destroy`というアノテーションを引数なしのメソッドに付ける。

```java
@Init
public void init() {
    (省略)
}

@Destroy
public void destroy() {
    (省略)
}
```

任意のイベントを発火、ハンドリングする機能をサポートする。
イベントの発火は`EventTrigger`インターフェースを通じて行われる。

```java
@Inject
private EventTrigger trigger;

public void login() {
    (省略)
    LoginSuccessEvent event = ...
    trigger.fire(event);
}
```

イベントのハンドリングを行うメソッドは`@Observes`を付けて引数にイベントを取る。

```java
@Observes
public void changeSessionId(LoginSuccessEvent event) {
    (省略)
}
```

## 制限事項

- `static`フィールド・`static`メソッドにはインジェクションできない
- より広いスコープのコンポーネントにはインジェクションできない
- 依存関係が循環してはいけない

より広いスコープのコンポーネントにはインジェクションできないとは例えば次のようなものを指す。

```java
@Singleton
public class Foo {

    @Inject
    private Bar bar;

    (省略)
}

@RequestScoped
public class Bar {
    (省略)
}
```

`Foo`はシングルトンなのでDIコンテナが破棄されるまで1つのインスタンスが生存する。
`Bar`はリクエストスコープなのでリクエストが終了すれば破棄されるが、シングルトンな`Foo`が保持しているのでリクエストが終了しても残り続けることになる。

コンポーネントのインスタンス生成を遅延させるための仕組みとして`javax.inject.Provider`がある。
これを使えば上記の例は解決できる。

```java
@Singleton
public class Foo {

    @Inject
    private Provider<Bar> barProvider;

    (省略)
}
```

`Bar`を使用するときは`barProvider.get()`でインスタンスを取得する。
その際、内部的にはDIコンテナからインスタンスを取得するのでスコープを気にする必要はない。


## 使用方法

`TraversalConfig`の実装クラスを作成する。

```java
package com.nablarch.example.app;

import nablarch.fw.dicontainer.annotation.auto.TraversalConfig;

public class DIConfig implements TraversalConfig {
}
```

次のパスへテキストファイルを作成する。

- `src/main/resources/META-INF/services/nablarch.fw.dicontainer.annotation.auto.TraversalConfig`

テキストファイルの内容は`TraversalConfig`実装クラスの完全修飾名。

```
com.nablarch.example.app.DIConfig
```

いくつかのコンポーネントを定義する。

```xml
  <component name="nablarchWebContextHandler" class="nablarch.fw.dicontainer.nablarch.NablarchWebContextHandler"/>
  <component name="annotationAutoContainerProvider" class="nablarch.fw.dicontainer.nablarch.AnnotationAutoContainerProvider">
    <property name="requestContextSupplier" ref="nablarchWebContextHandler"/>
    <property name="sessionContextSupplier" ref="nablarchWebContextHandler"/>
  </component>
```

ハンドラキュー構成に`nablarchWebContextHandler`を追加する。
`nablarchWebContextHandler`がリクエストスコープ、セッションスコープを使用するために`ExecutionContext`からコンテキストを構築してスレッドローカルに保存する。

```xml
<component name="webFrontController"
           class="nablarch.fw.web.servlet.WebFrontController">
  <property name="handlerQueue">
    <list>
      (中略)

      <component-ref name="nablarchWebContextHandler"/>

      (中略)
    </list>
  </property>
</component>
```

初期化が必要なコンポーネントとして`AnnotationAutoContainerProvider`を設定する。
この初期化処理でDIコンテナが作られる。

```xml
<component name="initializer"
           class="nablarch.core.repository.initialization.BasicApplicationInitializer">
  <property name="initializeList">
    <list>
      (省略)
      <component-ref name="annotationAutoContainerProvider"/>
    </list>
  </property>
</component>
```

`RoutesMapping`の定義へ`ContainerLookupDelegateFactory`を設定する。
`ContainerLookupDelegateFactory`がDIコンテナからアクションを取り出す役割を担っている。

```xml
<component name="packageMapping"
           class="nablarch.integration.router.RoutesMapping">
    (中略)
    <property name="delegateFactory">
        <component class="nablarch.fw.dicontainer.nablarch.ContainerLookupDelegateFactory"/>
    </property>
</component>
```

`BeanValidationStrategy`の定義へ`ContainerLookupBeanValidationFormFactory`を設定する。
`ContainerLookupBeanValidationFormFactory`がDIコンテナからフォームを取り出す役割を担っている。

```xml
<component name="validationStrategy" class="nablarch.common.web.validator.BeanValidationStrategy">
  <property name="formFactory">
    <component class="nablarch.fw.dicontainer.nablarch.ContainerLookupBeanValidationFormFactory" />
  </property>
</component>
```

参考：https://github.com/nablarch/nablarch-example-web/commit/3832b312f4905a9ee437d9266bd306cb687d4340

## 内部構造

### パッケージ構成

| パッケージ                                       | 説明                                                                                        |
|--------------------------------------------------|---------------------------------------------------------------------------------------------|
| `nablarch.fw.dicontainer`                        | DIコンテナのインターフェースといくつかのアノテーションが含まれる                            |
| `nablarch.fw.dicontainer.component`              | コンポーネント定義や検索キーが含まれる                                                      |
| `nablarch.fw.dicontainer.component.factory`      | コンポーネント定義や検索キーのファクトリーのインターフェースが含まれる                      |
| `nablarch.fw.dicontainer.component.impl`         | コンポーネントの構成要素の実装クラスが含まれる                                              |
| `nablarch.fw.dicontainer.component.impl.reflect` | リフレクションをラップするクラスが含まれる                                                  |
| `nablarch.fw.dicontainer.annotation`             | アノテーションをもとにDIコンテナを構築するためのクラスが含まれる                            |
| `nablarch.fw.dicontainer.annotation.auto`        | ディレクトリトラバーサルを行なって自動でDIコンテナを構築するためのクラスが含まれる          |
| `nablarch.fw.dicontainer.scope`                  | スコープに関連するクラスが含まれる                                                          |
| `nablarch.fw.dicontainer.container`              | DIコンテナの実装クラスが含まれる                                                            |
| `nablarch.fw.dicontainer.exception`              | 例外クラスが含まれる                                                                        |
| `nablarch.fw.dicontainer.event`                  | DIコンテナが発火させるイベントが含まれる                                                    |
| `nablarch.fw.dicontainer.web`                    | リクエストスコープ、セッションスコープのアノテーションが含まれる                            |
| `nablarch.fw.dicontainer.web.exception`          | Webに関する例外クラスが含まれる                                                             |
| `nablarch.fw.dicontainer.web.servlet`            | Servlet APIを使用してリクエストスコープ、セッションスコープを実現するためのクラスが含まれる |
| `nablarch.fw.dicontainer.web.context`            | リクエストスコープ、セッションスコープを実現するためのクラスが含まれる                      |
| `nablarch.fw.dicontainer.web.scope`              | リクエストスコープ、セッションスコープの実装クラスが含まれる                                |
| `nablarch.fw.dicontainer.nablarch`               | NablarchでDIコンテナを使用するためのクラスが含まれる                                        |
