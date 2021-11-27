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
 * Servlet implementation class LoginCheckServlet
 */
@WebServlet("/LoginCheckServlet")
public class LoginCheckServlet extends HttpServlet {
    // フィールド
    private static final long serialVersionUID = 1L;
    // 平のパスワードは、6文字以上、10文字以下の半角英数字 バリデーションに
    private final Pattern PATTERN_FLAT_PASSWORD = Pattern.compile("^[0-9a-zA-Z]{6,10}$");
   // メールアドレス バリデーションに
    private final Pattern PATTERN_MAIL = Pattern.compile("^[a-zA-Z0-9-_\\.]+@[a-zA-Z0-9-_\\.]+$");


    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginCheckServlet() {
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

        // リクエストパラメータの取得 index.jspからくる
        // 文字化け対策  今回はフィルターを作ったので、書かなくても大丈夫だが
        request.setCharacterEncoding("UTF-8");
        // バリデーションのエラーリストのインスタンスを newで確保
        List<String> errMsgList = new ArrayList<String>(); // エラーなければ、空のリスト  [] と表示されます

        String mail = request.getParameter("mail"); // メールアドレス データベースではユニーク一意制約をつけてある
        String flat_password = request.getParameter("flat_password"); // 平のパスワード
        // 入力チェック バリデーション
        if (flat_password == null || flat_password.length() == 0) {
            errMsgList.add("パスワードが入力されていません");
        } else if (!PATTERN_FLAT_PASSWORD.matcher(flat_password).matches()) {
            errMsgList.add("パスワードは6文字以上、10文字以下の半角英数字で入力してください");
        }
        if (mail == null || mail.length() == 0) {
            errMsgList.add("メールアドレスが入力されていません");
        } else if (!PATTERN_MAIL.matcher(mail).matches()) {
            errMsgList.add("メールアドレス形式で入力をしてください");
        }
         // index.jspに表示する
        // エラーリストに要素が１つ以上あったら、再入力をしてもらう
        if(errMsgList.size() > 0) {  // エラーがあった

              // リクエストスコープへ保存する
              request.setAttribute("errMsgList", errMsgList);  // エラーリストを送ります
              request.setAttribute("loginFailureMsg", "ログインに失敗しました。");  // index.jspに表示する
             //  request.setAttribute("flat_password", flat_password);  // パスワードは表示させないので送らないセキュリティのため
              request.setAttribute("mail", mail);
              // 再入力の キーaction 値re_enter もリクエストスコープに送る
              request.setAttribute("action", "re_enter");
              // フォワードする WebContentからの ルート相対パス  初め/ を書いておくこと index.jspなら  /  や  ./   だけでいい
              request.getRequestDispatcher("/index.jsp").forward(request, response);
              return; // リターンを書く return で、即終了させる　この行以降は実行されない

        } else {
            // エラーがなかったので、処理を進める
            // セッションを始めるけど、引数なしは引数trueと同じこと、既存のセッションがあったらそれを取得し、なかった場合に新しくセッションを生成する
        HttpSession  session = request.getSession();
        UserDao userDao = new UserDao();
        UserBean userBean = null;

        // セッションスコープのチェック 必要だこれ
        if (session == null) {
            // セッションがなかったら index.jspへ フォワード
            // リクエストスコープへ保存する
            request.setAttribute("loginFailureMsg", "ログインに失敗しました。");
           //  request.setAttribute("flat_password", flat_password);  // パスワードは表示させないので送らないセキュリティのため
            request.setAttribute("mail", mail);
            // 再入力の キーaction 値re_enter もリクエストスコープに送る
            request.setAttribute("action", "re_enter");
            // フォワードする WebContentからの ルート相対パス  初め/ を書いておくこと  index.jspなら  /  だけでも大丈夫  ./ でもいい
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            // request.getRequestDispatcher("./").forward(request, response);  // index.jspならこのパスでも良い
            return; // リターンを書く return で、即終了させる　この行以降は実行されない

        } else {
            // セッションがあればセッションスコープにUserBeanインスタンスを保存することによって、ログインをしている状態になる
            userBean = userDao.loginFind(mail); // ユニークのカラムのメールアドレスで検索してきたインスタンス

            if (userBean == null) {
                request.setAttribute("loginFailureMsg", "ログインに失敗しました。");
                //  request.setAttribute("flat_password", flat_password);  // パスワードは表示させないので送らないセキュリティのため
                 request.setAttribute("mail", mail);
                 // 再入力の キーaction 値re_enter もリクエストスコープに送る
                 request.setAttribute("action", "re_enter");
                 // フォワードする WebContentからの ルート相対パス  初め/ を書いておくこと  index.jspなら  /  だけでも大丈夫 ./ でもいい
                 request.getRequestDispatcher("/index.jsp").forward(request, response);
                 // request.getRequestDispatcher("./").forward(request, response);  // index.jspならこのパスでも良い
                return;
            } else {
                // まず、ソルトを取得しないといけないので、ユニークなパスワードから、UserBeanを取得して、主キーのidを取得する ソルトは、idを使ってますので
                String salt = String.valueOf(userBean.getId());  // ソルトを取得
                // ハッシュ化したパスワードを生成する  第2引数は、ソルトです int型のidをString型にして
                String hashed_pass = PasswordUtil.getSafetyPassword(flat_password, salt);
                if(userBean.getPass().equals(hashed_pass)) {  // パスワードが照合できたら、ログインできたとする
                    session.setAttribute("userBean", userBean);  // 無事にログインできたとして、セッションスコープに保存します
                //  ログイン成功 welcome.jspにフォワード WebContentからの ルート相対パス  初め/ を書いて
                    request.getRequestDispatcher("/WEB-INF/jsp/welcome.jsp").forward(request, response); // ここでリターン終了
                    return;
                } else { // パスワード照合できなかったら、ログインはできないので
                    request.setAttribute("loginFailureMsg", "ログインに失敗しました。");
                    //  request.setAttribute("flat_password", flat_password);  // パスワードは表示させないので送らないセキュリティのため
                     request.setAttribute("mail", mail);
                     // 再入力の キーaction 値re_enter もリクエストスコープに送る
                     request.setAttribute("action", "re_enter");
                     // フォワードする WebContentからの ルート相対パス  初め/ を書いておくこと  index.jspなら  /  だけでも大丈夫
                     request.getRequestDispatcher("/index.jsp").forward(request, response);
                     // request.getRequestDispatcher("./").forward(request, response);  // index.jspならこのパスでも良い
                    return;
                }
            }
        }
    }
}

}