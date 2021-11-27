package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * このクラスを使えば安全なパスワードを生成できる
 * @author ktkame
 * 
 */
public class PasswordUtil {
    // 「ハッシュ」・「ソルト」・「ストレッチング」の３つの対応を行うことを推奨
    // 強力な乱数生成器を使用して 16 バイト以上のソルトを作成
    // そのソルトとパスワードを PBKDF2 アルゴリズムに提供
    // PBKDF2 内のコアハッシュとして HMAC-SHA-256 を使用
    // 10,000 回以上繰り返して実行
    // 32 バイト (256 ビット) の出力を PBKDF2 から最終的なパスワードハッシュとして取り出します
    // Java SE 8でアルゴリズムとして「PBKDF2WithHmacSHA256」が使用可能ですのでこれを使う
    // 「HMAC-SHA-256」アルゴリズム     「SHA-256」がこのアルゴリズムで使用するハッシュ関数
    // ソルトについては 必ずしも乱数を使う必要は無い  ソルトとして ユーザーID(idカラム 自動採番のユニークな主キー)を使う
    // 「PBKDF2」ストレッチングのアルゴリズム      「HMAC-SHA-256」の「HMAC」は SHA-256」がこのアルゴリズムで使用するハッシュ関数
    // 「HMAC-SHA-256」は「PBKDF2」の中で今回は使われている  Java SE 8なら標準APIとしてPBKDF2とHMAC-SHA-256を一緒に使用するアルゴリズムが用意されている
    // ソルトとして ユーザーID(idカラム 自動採番のユニークな主キー)を使うことを想定しているため、そのままバイト配列にはせず、SHA-256でハッシュ化することで確実に16バイト以上(※SHA-256だと必ず32バイト)になるようにしています。
    // PBEKeySpecクラスのコンストラクタで生成される鍵の長さはHMAC-SHA-256に合わせて256を指定しています。その結果、SecretKeyクラスによって生成されるバイト配列は32バイトとなるため、16進数文字列へ変換後は64文字となります。

    /** パスワードを安全にするためのアルゴリズム */
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    /** ストレッチング回数 */
    private static final int ITERATION_COUNT = 10000;
    /** 生成される鍵の長さ */
    private static final int KEY_LENGTH = 256;

    /**
     *　平文のパスワードとソルトから安全なパスワードを生成し、返却します
     *
     * @param password 平文のパスワード
     * @param salt ソルト
     * @return 安全なパスワード
     */
    public static String getSafetyPassword(String password, String salt) {

        char[] passCharAry = password.toCharArray();
        byte[] hashedSalt = getHashedSalt(salt);

        PBEKeySpec keySpec = new PBEKeySpec(passCharAry, hashedSalt, ITERATION_COUNT, KEY_LENGTH);

        SecretKeyFactory skf;
        try {
            skf = SecretKeyFactory.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        SecretKey secretKey;
        try {
            secretKey = skf.generateSecret(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        byte[] passByteAry = secretKey.getEncoded();

        // 生成されたバイト配列を16進数の文字列に変換
        StringBuilder sb = new StringBuilder(64);
        for (byte b : passByteAry) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    /**
     * ソルトをハッシュ化して返却します
     * ※ハッシュアルゴリズムはSHA-256を使用
     *
     * @param salt ソルト
     * @return ハッシュ化されたバイト配列のソルト
     */
    private static byte[] getHashedSalt(String salt) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        messageDigest.update(salt.getBytes());
        return messageDigest.digest();
    }
}