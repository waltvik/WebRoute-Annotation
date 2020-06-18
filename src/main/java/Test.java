import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Test {
    private static HashMap<String, Method> pathMethods = new HashMap<String, Method>();

    public static void main(String[] args) throws Exception {
        for (Method m : Routes.class.getMethods()) {
            if (m.isAnnotationPresent(WebRoute.class)) {
                WebRoute webRoute = m.getAnnotation(WebRoute.class);
                pathMethods.put(webRoute.path(), m);

            }
        }
        System.out.println(pathMethods.toString());
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new MyHandler2());
        server.setExecutor(null); // creates a default executor
        server.start();

    }

    static class MyHandler2 implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "";
            t.sendResponseHeaders(200, response.length());
            System.out.println("local address " + t.getRequestURI());

            Class<?> type = null;
            try {
                type = Class.forName("Routes");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Constructor<?> constructor = null;
            try {
                assert type != null;
                constructor = type.getConstructor();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            Object instance = null;
            try {
                assert constructor != null;
                instance = constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            Object methodCallResult = null;
            try {
                methodCallResult = pathMethods.get(t.getRequestURI().toString()).invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            System.out.println(methodCallResult);
            assert methodCallResult != null;
            response = methodCallResult.toString();

            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }


}