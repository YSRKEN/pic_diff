# pic_diff

## 概要
2枚の画像の共通部分、もしくは非共通部分を抽出できるソフトです。
抽出する際、関係ない部分は透明色になります。
コマンドライン引数から、3枚以上の画像に対応可能です。

## インストール・アンインストール方法
 * インストール方法：zipファイルを解凍するだけです。jarファイルがプログラムの本体です。
 * アンインストール方法：ファイルをそのまま削除するだけです。レジストリは使用しません。

## ファイル
※必須ファイルは強調表示、そうでもないファイルは通常表示です。
 * **pic_diff.jar……実行ファイル。**
 * Readme.txt……この文章のことです。
 * pic_diff.java……ソースコードです。
 * 3files.bat……3枚の画像を読み込む際のバッチファイル

## 操作方法
1. そのままjarファイルをダブルクリックしてもいいですが、**第一引数には「同時に読み込む画像数」(N)を指定できます**。
「同時に読み込む画像数」の意味については後述しますが、とにかくNは2以上の自然数にしてください。
コマンドプロンプトで起動する際は「java -jar pic_diff」か「java -jar pic_diff N」となります。

2. 起動すると、次のような画面が出現します。
![スクショ](http://i.imgur.com/fqFj3Bo.png)
画面において、最終行以外は画像のパスを示します。参照ボタンは言うまでもありませんが、実はこの横長のテキストフィールドには画像ファイルをそのままドラッグ＆ドロップできます。

3. 最終行では、共通部分or非共通部分を抽出する際の設定を記述します。
4. まず、左下の「共通/差分(1)/差分(2)」は**抽出モード**です。「共通」にすると全画像の共通部分、「差分」にすると非共通部分をどの画像から取ってくるかを指定できます(カッコ内の数字が、画像1・画像2に対応)。
5. 左下からすぐ右の「RGB/Y」およびテキストフィールド(入力可能。**閾値**)は、**「共通部分である」と判定するための判断基準**を指します。RGBだとR・G・B値の差の2乗和(SSD)、Yだと[YCbCr](http://www.wdic.org/w/WDIC/YCbCr)で計算したYの値の差の2乗について、閾値以下なら共通、そうでない場合は非共通部分であると判断します。
6. 「保存」ボタンを押すと処理後、保存ダイアログが開きます。**保存形式は透明色付きpng固定**です。保存した際、拡張子の「.png」を忘れていた場合は自動補完されます。
7. 1.で説明したNを3以上にすると、抽出モードの「共通」が「全画像の共通」、「差分」が「どれかの画像の絵」に変化します。

## 注意
読み込まれる全画像のサイズが等しくないと事前チェックで弾かれます。

## 更新履歴
ver.1.0 2015/10/30
ソフトウェアを公開。

## 著作権表記
このソフトウェアはMITライセンスを適用しています。

## コメント
個人的に欲しかったので、実は一番助かっているのは私自身だったりします。
