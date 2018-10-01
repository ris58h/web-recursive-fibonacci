package ris58h.webrecfib.ratpack;

import ratpack.exec.Promise;
import ratpack.func.Action;
import ratpack.http.client.HttpClient;
import ratpack.server.RatpackServer;

import java.net.URI;

public class App {

    public static void main(String[] args) throws Exception {
        final int port = 8080;//TODO get it from args
        final HttpClient httpClient = HttpClient.of(Action.noop());
        RatpackServer.start(server -> server
                .serverConfig(cfg -> cfg.port(port))
                .handlers(chain -> chain.get(":n",
                        ctx -> {
                            long n = Long.parseLong(ctx.getPathTokens().get("n"));
                            if (n < 2) {
                                ctx.render(String.valueOf(n));
                            } else {
                                Promise<Long> n1 = fibonacci(n - 1, httpClient, port);
                                Promise<Long> n2 = fibonacci(n - 2, httpClient, port);
                                Promise<String> result = n1
                                        .flatMap(x -> n2.map(y -> y + x))
                                        .map(String::valueOf);
                                ctx.render(result);
                            }
                        }
                )));
    }

    private static Promise<Long> fibonacci(long n, HttpClient httpClient, int port) throws Exception {
        return httpClient.get(new URI("http://localhost:" + port + "/" + n))
                .onError(Throwable::printStackTrace)
                .map(resp -> Long.parseLong(resp.getBody().getText()));
    }
}
