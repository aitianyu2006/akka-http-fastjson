package akka.http.javadsl.marshallers;

import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.MediaTypes;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.testkit.JUnitRouteTest;
import akka.http.javadsl.testkit.TestRoute;
import akka.http.javadsl.testkit.TestRouteResult;
import com.alibaba.fastjson.TypeReference;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * JSON序列化与反序列化测试
 *
 * @author <a href="mailto:517926804@qq.com">Mike Chen</a>
 * @version 0.1.0
 * @since 0.1.0
 */
public class FastJSONTest extends JUnitRouteTest {
    private static final long serialVersionUID = 2128816647761613931L;
    private final TestRoute route = testRoute(
            pathEndOrSingleSlash(
                    () -> route(
                            get(() -> {
                                Map<String, Integer> data = new HashMap<>();
                                data.put("a", 1);
                                return complete(StatusCodes.OK, data, FastJSON.marshaller());
                            }),
                            post(() -> entity(FastJSON.unmarshaller(new TypeReference<Map<String, Integer>>(){}), data -> complete(StatusCodes.OK)))
                    )
            )
    );

    @Test
    public void testGet() {
        TestRouteResult response = route.run(HttpRequest.GET("/"));
        response.assertStatusCode(StatusCodes.OK)
                .assertContentType(ContentTypes.APPLICATION_JSON)
                .assertMediaType(MediaTypes.APPLICATION_JSON);

        Map<String, Object> data = response.entity(FastJSON.unmarshaller(new TypeReference<Map<String, Object>>(){}));
        assertTrue(data.containsKey("a"));
    }

    @Test
    public void testPost() {
        HttpRequest request = HttpRequest.POST("/").withEntity(MediaTypes.APPLICATION_JSON.toContentType(), "{\"a\": 1}");

        TestRouteResult response = route.run(request);
        response.assertStatusCode(StatusCodes.OK);
    }
}