package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.UserDao;
import model.UserBean;

/**
 * Servlet implementation class UserFormServlet
 * 画面表示をするサーブレット
 */
@WebServlet("/UserFormServlet")
public class UserFormServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserFormServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 文字化け対策  今回はフィルターを作ったので、書かなくても大丈夫だが
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action"); // "add" または "edit"
        UserDao userDao = new UserDao();  // 用意
        // セッションを作って、その中にUserBeanインスタンスを置かないと、フィルターに引っかかって、index.jspへ戻されてしまうので セッションを作る
        HttpSession session = request.getSession(); // 引数なしは 引数がtrueと同じこと

        // セッションスコープのチェック
        if (session == null) { // 必要
            // セッションがなかったら index.jspへ フォワード
            request.getRequestDispatcher("./").forward(request, response);
            return;
        } else {

            UserBean userBean = null;
            switch (action) {
            case "add":
                // 空の(フィールドが規定値のままの)userBeanをセッションに置く これがセッションスコープにない nullだと、フィルターが効くので index.jspへ転送されてしまう
                userBean = new UserBean(); // 空のインスタンス生成(各フィールドの値は、各データ型の既定値になっています)にしておけばいい nullじゃなければいいので nullだと、フィルターの作用でindex.jspへ転送されてしまう
                // セッションスコープに保存する これがセッションスコープにあれば、フィルターで戻されない
                session.setAttribute("userBean", userBean);

                break;
            case "edit":
                // userBean = (UserBean)session.getAttribute("userBean");

                // 自分のUserBeanインスタンスの内容を更新するので、セッションから取り出せます
                // 主キーから、UserBeanインスタンスを取得する welcome.jspのaリンクのクエリー文字列から取得できる
                //  こんなことしなくてもセッションスコープから取り出せばいいのでは？？？  idパラメータ要らなかった
                //                int id = Integer.parseInt(request.getParameter("id"));
                //                userBean = userDao.findById(id);
                //                if(userBean == null) { // 失敗
                //                	// 失敗のメッセージを
                //                	// リターン
                //                }
                // UserBeanインスタンスを取得できたら
                //  userBean = (UserBean)session.getAttribute("userBean");
                // 何もしなくていいので、もうログインしてるし、ここは経由せずに、welcomeからフォワードで
                // user_formに行ってもいいのじゃないか？？

                break;
            case "delete":
                // このままでいい
                break;
            }

            // リクエストスコープに保存する フォワード先で取得できる (リダイレクトでは渡せない)
            request.setAttribute("action", action); // "add" または "edit" "delete"を送ってる

            // このサーブレットでは、登録 編集 削除 画面にフォワードするだけです
            //   フォワードする 直接HTTPのURLを打ち込んでも、アクセスされないようにするにはWEB-INF配下にする WEB-INFの直下にjspフォルダを自分で作ってその中にフォワード先のjspファイルを置く
            // 現在は、フィルターをかけてるので、直接リクエストはされませんが
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/user_form.jsp");
            dispatcher.forward(request, response);
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}
