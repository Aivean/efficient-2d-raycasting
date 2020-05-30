package com.aivean.demo;


import com.aivean.raycasting2d.LightingBits64;
import com.aivean.raycasting2d.Rotation;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;

public class DemoHttpServer {


    final static int SUPERSAMPLE_FACTOR = 2;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new StaticHandler("/static"));
        server.createContext("/light", t -> {
            try {
                StringBuilder sb = new StringBuilder();
                try (Reader reader = new BufferedReader(new InputStreamReader(t.getRequestBody()))) {
                    int c = 0;
                    while ((c = reader.read()) != -1) {
                        sb.append((char) c);
                    }
                }

                String[] split = sb.toString().split(",");

                int size = (int) (Math.round(Math.sqrt(split.length)));

                LightingBits64 calc = new LightingBits64(size * SUPERSAMPLE_FACTOR, SUPERSAMPLE_FACTOR);

                int[][] input = new int[size * SUPERSAMPLE_FACTOR][size * SUPERSAMPLE_FACTOR];

                for (int y = 0; y < size * SUPERSAMPLE_FACTOR; y++) {
                    for (int x = 0; x < size * SUPERSAMPLE_FACTOR; x++) {
                        String s = split[(y / SUPERSAMPLE_FACTOR) * size + (x / SUPERSAMPLE_FACTOR)];
                        input[y][x] = s.equals("1") ? 1 : (s.equals("2") ? 2 : 0);
                    }
                }

                int[][] res = new int[size][size];

                long timeNS = System.nanoTime();
                for (Rotation r : Rotation.values()) {
                    calc.setInputRotated(input, r);
                    calc.recalculateLighting(1f);
                    calc.accumulateLightRotatated(res, r.opposite());
                }
                System.out.println((System.nanoTime() - timeNS) + " ns");

                sb = new StringBuilder();
                sb.append("[");
                for (int x = 0; x < size; x++) {
                    if (x != 0) sb.append(",");
                    sb.append("[");
                    for (int y = 0; y < size; y++) {
                        if (y != 0) sb.append(",");
                        sb.append(res[y][x]);
                    }
                    sb.append("]");
                }
                sb.append("]");

                Headers h = t.getResponseHeaders();
                h.set("Content-Type", "application/json");
                t.sendResponseHeaders(200, 0);
                t.getResponseBody().write(sb.toString().getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeQuietly(t.getResponseBody());
                closeQuietly(t.getRequestBody());
            }
        });
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class StaticHandler implements HttpHandler {

        private final String prefix;

        StaticHandler(String prefix) {
            this.prefix = prefix;
        }

        private static void serveStatic(HttpExchange t, String path) throws IOException {
            if (path.startsWith("/")) path = path.substring(1);

            OutputStream os = t.getResponseBody();
            InputStream is = null;
            try {
                is = StaticHandler.class.getClassLoader().getResourceAsStream(path);

                if (is == null) {
                    String response = "404 (Not Found)\n";
                    t.sendResponseHeaders(404, response.length());
                    os.write(response.getBytes());
                } else {

                    String mime = "text/html";
                    if (path.endsWith(".js")) mime = "application/javascript";
                    if (path.endsWith(".jpg")) mime = "image/jpeg";
                    if (path.endsWith("css")) mime = "text/css";

                    Headers h = t.getResponseHeaders();
                    h.set("Content-Type", mime);
                    t.sendResponseHeaders(200, 0);

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = is.read(buf)) > 0) {
                        os.write(buf, 0, len);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                t.sendResponseHeaders(500, 0);
                os.write("500".getBytes());
                PrintWriter pw = new PrintWriter(os);
                try {
                    e.printStackTrace(pw);
                } finally {
                    closeQuietly(pw);
                }
            } finally {
                closeQuietly(is);
                closeQuietly(os);
            }
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            URI uri = t.getRequestURI();
            String path = uri.getPath();
            if (!path.startsWith("/")) path = "/" + path;
            if (path.equals("/")) {
                path = path + "index.html";
            }
            serveStatic(t, this.prefix + path);
        }
    }

    static void closeQuietly(Closeable c) {
        if (c != null) try {
            c.close();
        } catch (Exception ignore) {
        }
    }

}
