package model;

import java.io.Serializable;

//自分で作成したクラスのインスタンスをスコープにおくには、Beanのクラスにして作らないとだめ
//Beanのクラスのルールにしたがってクラスを作ること スコープには、Beanクラスにしないと保存できないので 普通のString List Map など参照系のクラスのオブジェクトはスコープに置けますが、プリミティブ型は置けない
//自分で作成したクラスをインスタンスにしてスコープに置くには、Beanにしないといけない

// PostgreSQLは user が予約語なので、 userは "" で囲んでエスケープすれば使えますが、使わないほうがいいです いちいち select文などでも "user" と書かないとダメです。
// このモデル Beanクラスに関連するテーブルは users テーブルです  userが予約語なので、users としてます ちなみに usertableというテーブル名にはしないでください
//  ユーザーのカラムは schduleuser カラムとしてデータベースで作っています ちなみに、PostgreSQLは テーブル名カラム名全て小文字です
public class UserBean implements Serializable {

    /**
     * シリアル番号UID
     */
    private static final long serialVersionUID = -3625540505334959123L;

    private int id;  // 主キーでオートインクリメント(自動採番)なので 新規に作成する時には、INSERTで値を入れなくとも、自動で採番されるカラムです
    // idカラムには さらにインデックスもついてる
    private String name;  // ユーザー名

    // passカラムには、ハッシュ化された 67文字の文字列をデータベースに保存します f6e2e52bc7b6565928e2d0d18d1b074d5c21571da95d4ec250bc3168ddd37bd2  など
    private String pass;  // パスワード 暗号化は、ハッシュ関数と呼ばれる、ある文字列から復元不可能な文字列を生成するしくみを使います。

    private int roll;  // 管理者権限を持ったユーザーは「roll」カラムに「1」を設定します  管理者なら1、それ以外は0
    private String mail;  // データベースのカラムにはユニーク一意制約をつけてあります

    /**
     * 引数なしのコンストラクタ JavaBeansのルール
     * 引数ありのコンストラクタを作ると、デフォルトコンストラクタは作れないので、明示的に作る必要ある
     */
    public UserBean() {
        super();
    }

    /**
     * コンストラクタ
     * id以外のカラム引数にして 新規登録の時に使う PostgreSQLのシーケンス（データ型のserial）とは シーケンスとはINSERTで値を入れなくとも、自動で採番されるカラムなので、
     * INSERTの時に引数にidは要らない 引数4つ
     * @param name
     * @param pass
     * @param roll
     * @param mail
     */
    public UserBean(String name, String pass, int roll, String mail) {
        super();
        this.name = name;
        this.pass = pass;
        this.roll = roll;
        this.mail = mail;
    }

    /**
     * コンストラクタ
     * ログインの時に、UserBeanインスタンスをセッションスコープに保存するのために使うコンストラクタは、引数が５つ
     * @param id
     * @param name
     * @param pass
     * @param roll
     * @param mail
     */
    public UserBean(int id, String name, String pass, int roll, String mail) {
        super();
        this.id = id;
        this.name = name;
        this.pass = pass;
        this.roll = roll;
        this.mail = mail;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPass() {
        return pass;
    }

    public int getRoll() {
        return roll;
    }

    public String getMail() {
        return mail;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

}
