package filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * Servlet Filter implementation class CharacterEndodingFilter
 * 文字コードをUTF-8に設定するフィルタクラス
 * マッピング名   "/*"
 * 「@WebFilter」アノテーションが記述されています。作成するクラスをフィルタとする場合、
 * 「@WebFilter」アノテーションが必要です。なお、フィルタもWebアプリケーション開始時にインスタンス化されます。サーブレットもそうです
 *  アプリケーション終了するまで、その１つのインスタンスは保持し続ける
 * 「@WebFilter」アノテーションの（ ）内にURLパターンを指定することで該当するサーブレットクラスにフィルタを設定することができます。
 * 今回は、すべてのサーブレットクラスに設定するので、
 * 「@WebFilter("/*")」としています。 フィルタクラスはFilterインタフェースを実装して作成します。
 * Filterインタフェースには、以下の3つのメソッドが定義されており、これらのメソッドをすべて実装する必要があります。
 * 今回は、init()メソッドとdestroy()メソッドでは何も処理を行わないので、空で実装しています。
 * フィルタはサーブレットクラスだけでなく、JSPファイルやHTMLファイルにも適用することができます。
 *  「@WebFilter("/*")」は、すべてのサーブレットクラス以外にJSPファイル、HTMLファイルも含まれています
 */
@WebFilter("/*")
public class CharacterEndodingFilter implements Filter {

    /**
     * Default constructor.
     */
    public CharacterEndodingFilter() {
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
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        /*
         *  このメソッドが実行されるタイミングは 設定したサーブレットクラスをリクエストしたとき
           chain.doFilter よりも前に書くと前処理できる 一般的にサーブレットクラスのdoGet()メソッドやdoPost()メソッドの直前に実行させたい処理
          すべてのリクエストに対して文字コードをUTF-8に変換する
          フィルタはサーブレットクラスだけでなく、JSPファイルやHTMLファイルにも適用することができます。
         「@WebFilter("/*")」は、すべてのサーブレットクラス以外にJSPファイル、HTMLファイルも含まれています
        「chain.doFilter(request, response);」より前に記述する内容は前処理にあたり、
         一般的にサーブレットクラスのdoGet()メソッドやdoPost()メソッドの直前に実行させたい処理を記述します。
         *
         */

        request.setCharacterEncoding("UTF-8");

        //「chain.doFilter(request, response);」で、 次のフィルタやサーブレットクラスに処理を渡しています
        chain.doFilter(request, response);

        // 「chain.doFilter(request, response);」より後に記述する内容は後処理にあたりフィルタやサーブレットクラスが処理された後に実行されます。今回は必要なし
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
