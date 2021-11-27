package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.UserDao;
import model.UserBean;
import util.PasswordUtil;

/**
 * Servlet implementation class UserServlet
 * ユーザ新規登録 編集 削除する
 * 管理者だったら、一覧が見れ、他のユーザを新規も編集も削除もできるようにする 後で機能を追加する
 */
@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // フィールドとして
    // パターンチェック
    // 平のパスワードは、6文字以上、10文字以下の半角英数字
    private final Pattern PATTERN_FLAT_PASSWORD = Pattern.compile("^[0-9a-zA-Z]{6,10}$");
    // メールアドレス
    private final Pattern PATTERN_MAIL = Pattern.compile("^[a-zA-Z0-9-_\\.]+@[a-zA-Z0-9-_\\.]+$");

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // フォームを表示したらHTTPメソッドのPOSTで、このサーブレットに行って、ここでデータベースに登録します。
        // ユーザーをBeanのインスタンスにしたら、それを、セッションスコープに保存しておくこと、Loginした時と同じようにしておくこと
        // 文字化け対策  今回はフィルターを作ったので、書かなくても大丈夫だが
        //新規に登録して登録が成功したら、セッションスコープに UserBeanインスタンスを保存して上書きおきます。セッションスコープには空のUserBeanインスタンスがあるので上書きする
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action"); // "add" "edit" "delete"

        // バリデーションのエラーリストのインスタンスを newで確保
        List<String> errMsgList = new ArrayList<String>(); // エラーなければ、空のリスト  [] と表示されます
        // フォームからの取得
        String name = request.getParameter("name");
        String flat_password = request.getParameter("flat_password"); // 平のパスワード
        int roll = Integer.parseInt(request.getParameter("roll")); // 0 か 1     1 は管理者    選択しない時は -1が渡ってくるので、バリデーションにしてエラーにする
        String mail = request.getParameter("mail");
        int id = Integer.parseInt(request.getParameter("id")); // 編集と削除の時使える 新規の時 0

        // 入力チェック
        // 新規と編集の時だけ 削除の時には、スルーする 入力チェックする バリデーションです
        UserDao userDao = new UserDao();
        if(!action.equals("delete")) {
            if (name == null || name.length() == 0) { // nullチェックを先に書く
            errMsgList.add("名前が入力されていません");
        }

        if (flat_password == null || flat_password.length() == 0) {
            errMsgList.add("パスワードが入力されていません");
        } else if (!PATTERN_FLAT_PASSWORD.matcher(flat_password).matches()) {
            errMsgList.add("パスワードは6文字以上、10文字以下の半角英数字で入力してください");
        }

        // さらにメルアドはユニークであるため他のユーザとの同じメルアドはできないようにバリデーションする
        if (mail == null || mail.length() == 0) {
            errMsgList.add("メールアドレスが入力されていません");
        } else if (!PATTERN_MAIL.matcher(mail).matches()) {
            errMsgList.add("メールアドレス形式で入力をしてください");
        }

        boolean used = false;
        if(action.equals("add")) {
             used = userDao.newMailCheck(mail);
            if (used == true) {
                errMsgList.add("そのメールアドレスはすでに使用されています");
            }
        }
        if(action.equals("edit")) {
            used = userDao.editMailCheck(id,mail); // 自分自身のメルアドをまた同じにしても大丈夫
            if (used == true) {
                errMsgList.add("そのメールアドレスはすでに使用されています");
            }
        }
        }

        String form_msg = ""; // user_form.jspに表示する
        // エラーリストに要素が１つ以上あったら、再入力をしてもらう

        if (errMsgList.size() > 0) {
            form_msg = "入力に誤りがあります";
            // リクエストスコープへ保存する
            request.setAttribute("errMsgList", errMsgList); // エラーリストを送ります
            request.setAttribute("form_msg", form_msg);
            // リクエストスコープへ、保存します バラで送ります、フォームのフィールドが、平たいパスワードだから
            // スコープに置けるのは、参照型のインスタンスだけ、自分の作ったクラスは、JavaのBeanにしないとスコープに置けないので、クラスを作る時は、JavaBeanにすること、じゃないとスコープにはおけない 参照型でないと置けない Object型のサブクラスのインスタンスじゃないとだめ
            request.setAttribute("name", name);
            //   request.setAttribute("flat_password",flat_password); // パスワードはセキュリティのため表示させない
            request.setAttribute("roll", roll); // intだけど大丈夫だった 自動でIntegerのラッパークラスにボクシングすると思う
            request.setAttribute("mail", mail);
             request.setAttribute("action", action);
            request.setAttribute("id", id);

            request.setAttribute("re_enter", "re_enter");
            // フォワードする WebContentからの ルート相対パス  初め/ を書いておくこと
            request.getRequestDispatcher("/WEB-INF/jsp/user_form.jsp").forward(request, response);
            return; // リターンを書く return で、即終了させる　この行以降は実行されない
        } else {
            // エラーリストの要素が 0個の時は、処理を進めます。
            boolean success = false;
             HttpSession session = request.getSession(); // 引数なしは 引数 trueと同じ

            switch (action) {
            case "add":
                // 先に、ソルトを作るのに 主キーの値が必要 getNewId()
                int getNewId = userDao.getNewId(); // 最後に +1 した 主キーのid   OK 後でユーザが誰もいなかった時に 1が取れてるか確認してください
                if (getNewId == 0) { // 失敗

                    // 失敗のメッセージ そしてユーザ登録画面へ戻る フォームに入力した値をフォワード先に送って表示させる
                    form_msg = "ユーザー新規登録に失敗しました。";
                    request.setAttribute("form_msg", form_msg);
                    // リクエストスコープへ、保存します
                    request.setAttribute("name", name);
                    //   request.setAttribute("flat_password",flat_password); // パスワードはセキュリティのため表示させない
                    request.setAttribute("roll", roll); // intだけど大丈夫？ 自動でIntegerのラッパークラスにボクシングするか？？
                    request.setAttribute("mail", mail);
                    request.setAttribute("action", action);
                    request.setAttribute("id", id);

                    request.setAttribute("re_enter", "re_enter");

                    // フォワードする WebContentからの ルート相対パス  初め/ を書いておくこと
                    request.getRequestDispatcher("/WEB-INF/jsp/user_form.jsp").forward(request, response);
                    return; // リターンを書く return で、即終了させる　この行以降は実行されない
                } else { // 成功  一度全てのユーザーを削除してみて、新規登録にエラーがないか確認してください
                    // ソルトに 上で取得したgetNewId主キーを使います 第2引数です ハッシュ化したパスワードを取得する
                    String pass = PasswordUtil.getSafetyPassword(flat_password, Integer.toString(getNewId));
                    //データベースに登録する 成功したら、セッションスコープにUserBeanインスタンスを保存しておく セッションスコープにUserBeanインスタンスがあるかぎり、ログイン中となる
                    UserBean userBean = userDao.add(name, pass, roll, mail); // OK

                    if (userBean == null) { // 失敗

                        // 失敗のメッセージ そしてユーザ登録画面へ戻る フォームに入力した値をフォワード先に送って表示させる
                        form_msg = "ユーザー新規登録できませんでした。";
                        request.setAttribute("form_msg", form_msg);
                        // リクエストスコープへ、保存します UserBeanにはしないで、バラで送ります、平たいパスワードだから インスタンスじゃないとスコープにはおけない 参照型でないと置けない Object型のサブクラスのインスタンスじゃないとだめ
                        request.setAttribute("name", name);
                        //      request.setAttribute("flat_password",flat_password);  // パスワードはセキュリティのため表示させない
                        request.setAttribute("roll", roll); // intだけど大丈夫？ 自動でIntegerのラッパークラスにボクシングするか？？
                        request.setAttribute("mail", mail);
                        request.setAttribute("action", action);
                        request.setAttribute("id", id);

                        request.setAttribute("re_enter", "re_enter");

                        // フォワードする WebContentからの ルート相対パス  初め/ を書いておくこと
                        request.getRequestDispatcher("/WEB-INF/jsp/user_form.jsp").forward(request, response);
                        return; // リターンを書く return で、即終了させる　この行以降は実行されない
                    } else { // 成功したらセッションスコープにUserBeanインスタンスを保存しておく フィルターのために
                        // このUserBeanインスタンス  が セッションスコープにあるかぎり、あればログインしてることになるから
                     //    HttpSession session = request.getSession(); // 引数なしは 引数 trueと同じ
                        // セッションスコープのチェック 必要
                        if (session == null) {
                            // セッションがなかったら index.jspへ フォワードするので、リクエストスコープに保存する
                            request.setAttribute("userRegistFailureMsg", "ユーザ新規登録できませんでした");
                            request.getRequestDispatcher("./").forward(request, response);
                            return;
                        } else {
                            session.setAttribute("userBean", userBean); // 新規に登録してから、これでログインをしてることと同じになる
                            session.setAttribute("userAddMsg", "ユーザー新規登録しました。");
                            // 新規登録成功 welcome.jspにフォワード
                            request.getRequestDispatcher("/WEB-INF/jsp/welcome.jsp").forward(request, response);
                        }
                    }
                }

                break;
            case "edit":
                // 主キーで探す
                UserBean userBean = userDao.findById(id);
                //セッターを使うフィールドを上書きする
                userBean.setName(name);
                // ハッシュ化したパスワードを取得する
                String pass = PasswordUtil.getSafetyPassword(flat_password, Integer.toString(id));
                userBean.setPass(pass);
                userBean.setMail(mail);
                userBean.setRoll(roll);
                // セッターで更新したUserBeanインスタンスをデータベースに更新する
                success = userDao.update(userBean);
                if(success == false) { // 失敗

                      // 失敗のメッセージ そしてユーザ登録画面へ戻る フォームに入力した値をフォワード先に送って表示させる
                    form_msg = "ユーザー情報を編集できませんでした。";
                    request.setAttribute("form_msg", form_msg);
                    // リクエストスコープへ、保存します UserBeanにはしないで、バラで送ります、平たいパスワードだから インスタンスじゃないとスコープにはおけない 参照型でないと置けない Object型のサブクラスのインスタンスじゃないとだめ
                    request.setAttribute("name", name);
                    //      request.setAttribute("flat_password",flat_password);  // パスワードはセキュリティのため表示させない
                    request.setAttribute("roll", roll); // intだけど大丈夫？ 自動でIntegerのラッパークラスにボクシングするか？？
                    request.setAttribute("mail", mail);
                    request.setAttribute("action", action);
                    request.setAttribute("id", id);

                    request.setAttribute("re_enter", "re_enter");

                    // フォワードする WebContentからの ルート相対パス  初め/ を書いておくこと
                    request.getRequestDispatcher("/WEB-INF/jsp/user_form.jsp").forward(request, response);
                    return; // リターンを書く return で、即終了させる　この行以降は実行されない

                } else { // 成功したら
                    session.setAttribute("userBean", userBean); // 編集したUserBeanをセッションスコープへ置いて上書きしておく すでに、セッションスコープにはUserBeanが保存してあるので
                    session.setAttribute("userEditMsg", "ユーザー情報を変更しました。");
                    // 編集成功したら welcome.jspにフォワード
                    request.getRequestDispatcher("/WEB-INF/jsp/welcome.jsp").forward(request, response);
                }

                break;

            case "delete":
                 // 主キーで削除する
                 String str = userDao.delete(id);

                 if(str.equals("failure")) {
                     form_msg = "ユーザー情報を削除できませんでした";
                 } else if(str.equals("relationFailure")) {
                     form_msg = "このユーザには登録してあるスケジュールが存在するため、ユーザー情報を削除できませんでした";

                 }


                 if(str.equals("failure") || str.equals("relationFailure")) { // 失敗
                      // 失敗のメッセージ そしてユーザ登録画面へ戻る フォームに入力した値をフォワード先に送って表示させる

                     request.setAttribute("form_msg", form_msg);

                     // リクエストスコープへ、保存します UserBeanにはしないで、バラで送ります、平たいパスワードだから インスタンスじゃないとスコープにはおけない 参照型でないと置けない Object型のサブクラスのインスタンスじゃないとだめ
                     request.setAttribute("name", name);
                     //      request.setAttribute("flat_password",flat_password);  // パスワードはセキュリティのため表示させない
                     request.setAttribute("roll", roll); // intだけど大丈夫？ 自動でIntegerのラッパークラスにボクシングするか？？
                     request.setAttribute("mail", mail);
                     request.setAttribute("action", action);
                     request.setAttribute("id", id);

                     request.setAttribute("re_enter", "re_enter");

                     // フォワードする WebContentからの ルート相対パス  初め/ を書いておくこと
                     request.getRequestDispatcher("/WEB-INF/jsp/user_form.jsp").forward(request, response);
                     return; // リターンを書く return で、即終了させる　この行以降は実行されない

                 } else {  // 成功
                     // 削除をしたら、セッションスコープ自体を破棄してログアウトします
                     // ログアウトするには
                     // 既存のセッションスコープを取得getSession() は　getSession(true) と同じ
                     session = request.getSession();
                    // セッションスコープを破棄  セッションスコープ自体を破棄する
                    session.invalidate();
                    request.setAttribute("userDeleteMsg", "ユーザーを削除しました");
                    // ログアウト画面 logout.jsp にフォワードする WebContentからの ルート相対パスで指定する
                 // フォワード処理
                    request.getRequestDispatcher("/WEB-INF/jsp/logout.jsp").forward(request, response);
                 }
                break;
            }

        }
    }
}
