package vn.vnpt.sso.util;

import org.springframework.util.SerializationUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;

public class CookieUtils {

    /**
     * Hàm lấy giá trị cookie theo tên
     *
     * @param request thông tin khi có yêu cầu
     * @param name    giá trị cookie cần tìm
     * @return trả về giá trị cookie
     */
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        // Nhận list cookie từ request trả về
        Cookie[] cookies = request.getCookies();

        // Nếu danh sách trả về có data
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                // Tìm cookie name theo biến name nhận vào
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }


    /**
     * Hàm gán giá trị cookie và trả lại cho client
     *
     * @param response gửi phản hồi
     * @param name     Tên cookie
     * @param value    Giá trị cho cookie
     * @param age      thời hạn của cookie
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int age) {
        // Khởi tạo 1 cookie với tên và giá trị nhận được
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/"); // set url khi cookie
        cookie.setHttpOnly(true);
        cookie.setMaxAge(age);
        response.addCookie(cookie);
    }

    /**
     * Xoá cookie
     *
     * @param request  thông tin khi có yêu cầu
     * @param response gửi phản hồi thông tin
     * @param name     Tên cookie
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        // Nếu trả về có data
        Optional<Cookie> cookieExists = getCookie(request, name);
        if (cookieExists.isPresent()) {
            Cookie cookie = cookieExists.get();
            cookie.setValue("");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }


    /**
     * Hàm mã hoá base64
     *
     * @param object Đối tượng nhận được
     * @return trả về value mã hoá
     */
    public static String serialize(Object object) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
    }

    /**
     * Decode base64
     *
     * @param cookie giá trị cookie cần decode
     * @param cls    Class triển khai
     * @param <T>    Class triển khai
     * @return trả về giá trị được decode
     */
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.getValue())));
    }


}
