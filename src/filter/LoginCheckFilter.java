package filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import model.UserBean;

/**
 * Servlet Filter implementation class CharacterEndodingFilter
 * ログインの有無チェックを行うフィルタクラス
 * マッピング名
 *
 * 「@WebFilter」アノテーションが記述されています。作成するクラスをフィルタとする場合、
 * 「@WebFilter」アノテーションが必要です。なお、フィルタもWebアプリケーション開始時にインスタンス化されます。サーブレットもそうです
 *  アプリケーション終了するまで、その１つのインスタンスは保持し続ける
 * 「@WebFilter」アノテーションの（ ）内にURLパターンを指定することで該当するサーブレットクラスにフィルタを設定することができます。
 * すべてのサーブレットクラスに設定するなら、
 * 「@WebFilter("/*")」でできます フィルタクラスはFilterインタフェースを実装して作成します。
 * Filterインタフェースには、以下の3つのメソッドが定義されており、これらのメソッドをすべて実装する必要があります。
 * 今回は、init()メソッドとdestroy()メソッドでは何も処理を行わないので、空で実装しています。
 * フィルタはサーブレットクラスだけでなく、JSPファイルやHTMLファイルにも適用することができます。
 *  「@WebFilter("/*")」は、すべてのサーブレットクラス以外にJSPファイル、HTMLファイルも含まれています
 * @WebFilter("/*") にしないとうまく機能しない
 *
 */
@WebFilter("/*")
public class LoginCheckFilter implements Filter {

    /**
     * Default constructor.
     */
    public LoginCheckFilter() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @see Filter#destroy()
     */
    public void destroy() {
         // このメソッド   destroy  init  とは必ず実装する必要があります。
        // 今回は空で実装しています。 {} があれば実装していることになります。
        // このメソッドの実行のタイミング  フィルタのインスタンスが破棄されるとき
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     * このメソッドが実行されるタイミングは 設定したサーブレットクラスをリクエストしたとき
       chain.doFilter よりも前に書くと前処理できる 一般的にサーブレットクラスのdoGet()メソッドやdoPost()メソッドの直前に実行させたい処理

      マッピングに書けば、フィルタはサーブレットクラスだけでなく、JSPファイルやHTMLファイルにも適用することができます。
     「@WebFilter("/*")」は、すべてのサーブレットクラス以外にJSPファイル、HTMLファイルも含まれています
    「chain.doFilter(request, response);」より前に記述する内容は前処理にあたり、
     一般的にサーブレットクラスのdoGet()メソッドやdoPost()メソッドの直前に実行させたい処理を記述します。
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 前処理
        // セッションを継続する場合も、HttpServletRequestのgetSessionメソッドを実行します。このときの引数はfalseにする必要があります
        // セッションの取得  引数に"false"を指定した場合、セッションが存在しない場合にはnullが帰ってきます。

        // doFilterメソッドの 引数が  ServletRequest request, ServletResponse responseなので、「HttpServletRequest」型へのキャストが必要となります
         HttpSession session = ((HttpServletRequest) request).getSession(false); // 既存のセッションがあったら、継続するため falseを引数にする
        // セッションおよびログインのチェック
        if (session == null) {  // セッションが存在しない
          // index.jsp へ フォワード
          ((HttpServletRequest) request).getRequestDispatcher("./").forward(request, response);
        } else {  // 既存のセッションが存在したので継続する 引数に falseを設定したので、既存のセッションを継続します
          // ログインユーザの取得  セッションからUserBeanインスタンスを取得する
            UserBean userBean = (UserBean) session.getAttribute("userBean");

          if (userBean == null) {  // ログインしてなかった(直接リクエストされたなどで)   index.jspへフォワードさせる
            ((HttpServletRequest) request).getRequestDispatcher("./").forward(request, response);
          }
          chain.doFilter(request, response);  // 次のフィルタやサーブレットクラスに処理を渡しています

          // ここに記述する内容は後処理にあたりフィルタやサーブレットクラスが処理された後に実行されます。今回は必要なし

        }
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig fConfig) throws ServletException {
        // このメソッド   destroy  init  とは必ず実装する必要があります。
        // 今回は空で実装しています。 {} があれば実装していることになります。
        // このメソッドの実行のタイミング  フィルタがインスタンス化されたとき
    }

}
