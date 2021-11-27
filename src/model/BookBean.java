package model;

import java.io.Serializable;

//自分で作成したクラスのインスタンスをスコープにおくには、Beanのクラスにして作らないとだめ
//Beanのクラスのルールにしたがってクラスを作ること スコープには、Beanクラスにしないと保存できないので 普通のString List Map など参照系のクラスのオブジェクトはスコープに置けますが、プリミティブ型は置けない
//自分で作成したクラスをインスタンスにしてスコープに置くには、Beanにしないといけない

//PostgreSQLは user が予約語なので、 userは "" で囲んでエスケープすれば使えますが、使わないほうがいいです いちいち select文などでも "user" と書かないとダメです。
//このモデル Beanクラスに関連するテーブルは users テーブルです  userが予約語なので、users としてます ちなみに usertableというテーブル名にはしないでください
//ユーザーのカラムは schduleuser カラムとしてデータベースで作っています ちなみに、PostgreSQLは テーブル名カラム名全て小文字です



public class BookBean implements Serializable {
    // privateフィールド
    /**
     * シリアル番号UID
     */
    private static final long serialVersionUID = -6695539307524563880L;

    private int id;  // 主キーでオートインクリメント(自動採番)なので 新規に作成する時には、INSERTで値を入れなくとも、自動で採番されるカラムです
    // idカラムには さらにインデックスもついてる

    private String title;  // 本のタイトル名

    private String authors;  // 本の著者(多数でも、一つの文字列に)

    private String publisher;  // 出版社

    private Integer publishYear;   // 出版年  PostgreSQLはカラム名は、全てが小文字になるので注意

    /**
     * 引数なしのコンストラクタ JavaBeansのルール
     * 引数ありのコンストラクタを作ると、デフォルトコンストラクタは作れないので、明示的に作る必要ある
     */
    public BookBean() {
        super();
        // TODO 自動生成されたコンストラクター・スタブ
    }

    /**
     * 引数４つのコンストラクタ
     * id以外のカラム引数にして 新規登録の時に使う PostgreSQLのシーケンス（データ型のserial）とは シーケンスとはINSERTで値を入れなくとも、自動で採番されるカラムなので、
     * INSERTの時に引数にidは要らない 引数4つ
     * @param title
     * @param authors
     * @param publisher
     * @param publishYear
     */
    public BookBean(String title, String authors, String publisher, Integer publishYear) {
        super();
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publishYear = publishYear;
    }


    /**
     * 引数５つのコンストラクタ
     * @param id
     * @param title
     * @param authors
     * @param publisher
     * @param publishYear
     */
    public BookBean(int id, String title, String authors, String publisher, Integer publishYear) {
        super();
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publishYear = publishYear;
    }
    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getAuthors() {
        return authors;
    }
    public String getPublisher() {
        return publisher;
    }
    public Integer getPublishYear() {
        return publishYear;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setAuthors(String authors) {
        this.authors = authors;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public void setPublishYear(Integer publishYear) {
        this.publishYear = publishYear;
    }



}
