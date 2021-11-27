package dao;
/*
WebContent   WEB-INF  libフォルダの中に、ドライバー入れたら、ビルドパス構成で適用しないといけません。
右クリックで ビルドパス ビルドパスの構成 ライブラリタグ クラスパス  JARファイルの追加 で、postgresql-4.2.23.jarを
今のプロジェクトの中のlibフォルダに入れて適用してください
/Applications/Eclipse_2020_12.app/Contents/workspace/LocalDateTimeSchedule/Webcontent/WEB-INF/lib
  にして 適用する
*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.postgresql.util.PSQLException;

import model.UserBean;

public class UserDao {

    final String DRIVER_NAME = "org.postgresql.Driver";
    final String JDBC_URL = "jdbc:postgresql://localhost:5432/libservlet";
    final String DB_USER = "postgres";
    final String DB_PASS = "postgres";

    /**
     * ユーザー新規登録
     * @param name
     * @param pass
     * @param roll
     * @param mail
     * @return userBean
     */
    public UserBean add(String name, String pass, int roll, String mail) {
        // 戻り値の　UserBeanインスタンスには、主キーがちゃんと入ってるのをリターンする。そして、それを、セッションスコープに保存をしてログインした状態とする
        UserBean userBean = null; // 主キーもきちんと入ってる完全形のUserBean これを後でセッションスコープに置きます
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // JDBCドライバを読み込み
            Class.forName(DRIVER_NAME);
            // データベースへ接続
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);

            // PostgreSQLだと、全部小文字でカラム名やテーブル名を書くこと id は、自動採番なので、書かない
            // user は PostgreSQLの予約語のため、なるべく使わない ""で囲んでエスケープすれば使えるけど、使わないほうがいい
            // 主キーの id が serial です シーケンス（データ型のserial）とは 自動採番するカラムです。
            // シーケンスとはINSERTで値を入れなくとも、自動で採番される列で、CREATE SEQUENCE文で作成することができます。またテーブル作成時、データ型に「serial」を指定した場合も同じくシーケンスとなります。シーケンスは自動で1から採番され、＋1ずつされます。
            String sql = "insert into users (name, pass, roll, mail) values (?, ?, ?::integer, ?)";

            // PostgresSQLは、Statement生成時に、Statement.RETURN_GENERATED_KEYSを指定するとStatement#getGeneratedKeysでそのテーブルの全カラムの情報が取得される。

            //  pstmt = conn.prepareStatement(sql);

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // insertする時は、主キーは自動採番 PostgreSQLで serial としてるので、書かないでも大丈夫
            pstmt.setString(1, name);
            pstmt.setString(2, pass);
            pstmt.setInt(3, roll);
            pstmt.setString(4, mail);

            // generateKeyを取得したい 解決策：追加 Statement.RETURN_GENERATED_KEYS でもつけるとエラー文法的に
            // executeUpdate() 注意  戻り値を取得するような書き方をするとエラーになります！！
            pstmt.executeUpdate(); // ここに引数を入れてはいけません 戻り値は変更したデータの数

            // 取れる自動生成した主キーの値
            rs = pstmt.getGeneratedKeys(); // この Statement オブジェクトを実行した結果として作成された自動生成キーを取得します。この Statement オブジェクトがキーを生成しなかった場合は、空の ResultSet オブジェクトが返されます。

            if (rs.next()) {
                pstmt.getMetaData().getColumnCount();

                int id = rs.getInt(1); // 引数は 先頭なので 1を指定する  注: 自動生成キーを表す列が指定されなかった場合、JDBC ドライバ実装では、自動生成キーを表すのに最適な列を判断します。
               //  int id = rs.getInt("id");
                //  PostgreSQLはgetGeneratedKeys()メソッドをサポートしてます  JDBC ドライバがこのメソッドgetGeneratedKeys() をサポートしない場合例外発生します PostgreSQLはサポートしてます
                //   String name = rs.getString("name");  // 取ることも可能です
                //   String pass = rs.getString("pass");  // 取ることも可能です
                //  int roll = rs.getInt("roll");
                //  String mail = rs.getString("mail");

                userBean = new UserBean(id, name, pass, roll, mail); // idが取得できれば、これできそうだな
            }

        } catch (SQLException | ClassNotFoundException e) {
            // データベース接続やSQL実行失敗時の処理
            // JDBCドライバが見つからなかったときの処理
            e.printStackTrace();
            return null; // 失敗したら false返す
        } finally {
            // PrepareStatementインスタンスのクローズ処理
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    // クローズ処理失敗時の処理
                    e.printStackTrace();
                    return null; // 失敗したら false返す
                }
            }
            // データベース切断
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // データベース切断失敗時の処理
                    e.printStackTrace();
                    return null; // 失敗したら false返す
                }
            }
        }
        return userBean;

    }

    /**
     * ソルトのために取得する
     * 一番最後のユーザの主キーidの値を取得する データベースで何らかのエラーがあった時は 0 を返す
     * @return newId
     */
    public int getNewId() {
        // これはソルトが必要なので
        int newId = 0; // 0で初期化してる
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // JDBCドライバを読み込み
            Class.forName(DRIVER_NAME);
            // データベースへ接続
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);

            String sql = "select id from users order by id desc limit 1";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) { //  limit 1 だから、１件しか取得しないので whileじゃなくてifでもいい
                int lastId = rs.getInt("id"); // カーソルが trueだったらつまり、次に行があったので
                //  id = rs.getInt(1);  // 引数 1  でもいいです
                newId = lastId + 1;
            } else { // カーソルが falseを示した時、つまり、何も次にない状態の時は、まだ、１つもデータがないので id = 1を入れる
                newId = 1; // 最初のデータとなる
            }
        } catch (Exception e) { // Exceptionクラスのインスタンスでキャッチする
            e.printStackTrace();
            return 0; // 失敗したら、0を返す
        } finally {
            // PrepareStatementインスタンス、ResultSetインスタンスのクローズ処理
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // クローズ処理失敗時の処理
                    e.printStackTrace();
                    return 0; // 失敗したら、0を返す
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    // クローズ処理失敗時の処理
                    e.printStackTrace();
                    return 0;// 失敗したら、0を返す
                }
            }
            // データベース切断
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // データベース切断失敗時の処理
                    e.printStackTrace();
                    return 0;// 失敗したら、0を返す
                }
            }
        }
        return newId;
    }

    /**
     * ログイン時はメルアドでまず検索する メルアドはユニーク(一意制約つけたカラムなので検索できる)
     *
     * @param mail
     * @return userBean
     */
    public UserBean loginFind(String mail) {
        UserBean userBean = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // JDBCドライバを読み込み
            Class.forName(DRIVER_NAME);
            // データベースへ接続
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);

            String sql = "select * from users where mail = ?";

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, mail);
            rs = pstmt.executeQuery();
            if (rs.next()) { // 一意制約のカラムを元にして検索したので、1件だけ返るので whileじゃなくて ifでもいい
                int id = rs.getInt("id");
                String name = rs.getString("name");
                // パスワードは、ハッシュ化されたものが、passカラム名でデータベースに格納されてる
                String pass = rs.getString("pass");
                int roll = rs.getInt("roll");
                // String mail = rs.getString("mail");
                userBean = new UserBean(id, name, pass, roll, mail);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null; // エラーの時は、nullを返すようにする。
        } finally {
            if (rs != null) { //close()する順番は、逆からする
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null; // エラーの時は、nullを返すようにする。
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null; // エラーの時は、nullを返すようにする。
                }
            }
            // データベース切断
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null; // エラーの時は、nullを返すようにする。
                }
            }
        }
        return userBean;
    }

    /**
     * 主キーからユーザー検索する
     * @param id
     * @return userBean
     */
    public UserBean findById(int id) {

        UserBean userBean = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "select * from users where id = ?::integer";

        try {
            // JDBCドライバを読み込み
            Class.forName(DRIVER_NAME);
            // データベースへ接続
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();
            if (rs.next()) { // 主キーで検索してるので、得られる件数は１件のみなので、whileじゃなくて ifでいい
                String name = rs.getString("name");
                String pass = rs.getString("pass");
                int roll = rs.getInt("roll");
                String mail = rs.getString("mail");
                userBean = new UserBean(id, name, pass, roll, mail);
            }
        } catch (SQLException | ClassNotFoundException e) {
            // データベース接続やSQL実行失敗時の処理
            // JDBCドライバが見つからなかったときの処理
            e.printStackTrace();
            return null; // 失敗したらnull返す
        } finally {
            // PrepareStatementインスタンスのクローズ処理
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    // クローズ処理失敗時の処理
                    e.printStackTrace();
                    return null; // 失敗したら false返す
                }
            }
            // データベース切断
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // データベース切断失敗時の処理
                    e.printStackTrace();
                    return null; // 失敗したら false返す
                }
            }
        }
        return userBean;
    }

    /**
     * ユーザ新規の時にだけ使うバリデーション用
     * @param id
     * @param mail
     * @return true 成功<br> false 失敗
     */
    public boolean newMailCheck(String mail) {
        String getMail = "";
        boolean used = false; // すでに使われているかどうか
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // JDBCドライバを読み込み
            Class.forName(DRIVER_NAME);
            // データベースへ接続
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
            // 新規ではこれでいい
            String sql = "select mail from users where mail = ?";

            // 編集時だったら、
            // パスワード変更の時は 自分自身のパスワード以外で同じものがあったらだめ、にしないといけないので
            // 例:  select mail from usertable where id != 1 and  mail = 'root@root.com';
            // 編集だったらこれ
            //    String sql = "select mail from usertable where id != ?::integer and  mail = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, mail);

            // 編集だったらこれ
            //            pstmt.setInt(1,id);
            //            pstmt.setString(2, mail);
            rs = pstmt.executeQuery();
            while (rs.next()) { // ユニークなカラムだから whileじゃなくて ifでもいい
                getMail = rs.getString("mail");
                if (getMail.equals(mail)) { // whileを抜ける条件
                    used = true; // すでに使われている
                    break; // whileを抜ける
                } else {
                    used = false; // まだ使われていない
                    break; // whileを抜ける
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            //   return null; // エラーの時は、nullを返すようにする。
        } finally {
            if (rs != null) { //close()する順番は、逆からする
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    // return null; // エラーの時は、nullを返すようにする。
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    //  return null; // エラーの時は、nullを返すようにする。
                }
            }
            // データベース切断
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    //  return null; // エラーの時は、nullを返すようにする。
                }
            }
        }
        return used;
    }

    /**
     * ユーザ編集の時だけ使うバリデーション用
     * メールアドレスが、同じものがあるかどうか バリデーションチェックする ユニークなカラムmail
     * @param mail
     * @param id
     * @return true:もうすでに登録されているので使用できない<br /> false:まだ使われていない
     */
    public boolean editMailCheck(int id, String mail) { // 第1引数は、自分のid
        String getMail = "";
        boolean used = false; // すでに使われているかどうか
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // JDBCドライバを読み込み
            Class.forName(DRIVER_NAME);
            // データベースへ接続
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
            //  String sql = "select mail from usertable where mail = ?"; //これじゃだめ
            // パスワード変更の時は 自分自身のパスワード以外で同じものがあったらだめ、にしないといけないので
            // 例:  select mail from usertable where id != 1 and  mail = 'root@root.com';

            String sql = "select mail from users where id != ?::integer and  mail = ?";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, id);
            pstmt.setString(2, mail);
            rs = pstmt.executeQuery();
            while (rs.next()) { // ユニークなカラムだから whileじゃなくて ifでもいい
                getMail = rs.getString("mail");
                if (getMail.equals(mail)) { // whileを抜ける条件
                    used = true; // すでに使われている
                    break; // whileを抜ける
                } else {
                    used = false; // まだ使われていない
                    break; // whileを抜ける
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            //   return null; // エラーの時は、nullを返すようにする。
        } finally {
            if (rs != null) { //close()する順番は、逆からする
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    // return null; // エラーの時は、nullを返すようにする。
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    //  return null; // エラーの時は、nullを返すようにする。
                }
            }
            // データベース切断
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    //  return null; // エラーの時は、nullを返すようにする。
                }
            }
        }
        return used;
    }

    /**
     * ユーザー情報を編集
     * @param userBean
     * @return true 成功<br> false 失敗
     */
    public boolean update(UserBean userBean) {

         Connection conn = null;
         PreparedStatement pstmt = null;
         try {
             Class.forName(DRIVER_NAME);
             conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);

            String sql = "update users set (name, pass, mail) = (?, ?, ?) where id = ?::integer";
             pstmt = conn.prepareStatement(sql);

             pstmt.setString(1, userBean.getName());
             pstmt.setString(2, userBean.getPass());
             pstmt.setString(3, userBean.getMail());
             pstmt.setInt(4, userBean.getId());

            int result = pstmt.executeUpdate();  // 更新に成功した件数が返る
            if(result != 1) {  // where句で 主キーで検索してるので、返る件数は成功したら、１件 whileでもいいけど ifでもいい
                return false;  // 失敗したらfalseを返す
            }

         }catch (SQLException | ClassNotFoundException e) {
             // データベース接続やSQL実行失敗時の処理
             // JDBCドライバが見つからなかったときの処理
             e.printStackTrace();
             return false; // 失敗した時に、falseを返す
         } finally {
             // PrepareStatementインスタンスのクローズ処理 順番は逆からクローズする
             if (pstmt != null) {
                 try {
                     pstmt.close();
                 } catch (SQLException e) {
                     // クローズ処理失敗時の処理
                     e.printStackTrace();
                     return false; // 失敗した時に、falseを返す
                 }
             }
             // データベース切断
             if (conn != null) {
                 try {
                     conn.close();
                 } catch (SQLException e) {
                     // データベース切断失敗時の処理
                     e.printStackTrace();
                     return false; // 失敗した時に、falseを返す
                 }
             }
         }
         return true;
     }

    /**
     * ユーザ削除 親テーブルでリレーションが子テーブルのscheduleなので、紐づいているscheduleのデータがあれば、エラーで削除できない
     * @param id
     * @return "ok":成功<br />  "failure":データベースなどの失敗<br />  "relationFailure": リレーションがあるので削除できなかった時の返り値
     */
    public String delete(int id) {


        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);

         // 注意 PostgreSQLではテーブル名カラム名全て小文字で  whereをつけ忘れないように

           String sql = "delete from users where id = ?::integer";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            // 成功したら、executeUpdateメソッドの戻り値は、更新された行数を表します。
            int result = pstmt.executeUpdate();  // リレーションのあるユーザを削除しようとすると、ここで、org.postgresql.util.PSQLExceptionエラー発生
//            update or delete on table "usertable" violates foreign key constraint "schedule_userid_fkey" on table "schedule"
//            詳細: Key (id)=(22) is still referenced from table "schedule".

            if(result != 1) {  // 失敗

                return "failure";   // ここでメソッドが即終了し、引数の strを呼び出し元へ返す
            }
            // 更新された行が結果1 だったら成功
        } catch (PSQLException e) {
            // リレーションのあるユーザを削除しようとすると、エラーになるのでここでキャッチして処理する
            //  "relationFailure" を返すようにします
            return "relationFailure";  // ここでメソッドが即終了し、引数の strを呼び出し元へ返す
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return "failure";
        } finally {
            // PrepareStatementインスタンスのクローズ処理
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return "failure";
                }
            }
         // データーベース切断
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return "failure";
                }
            }
        }
        return "ok";
    }
}