/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.benchto.driver.graphite;

import io.trino.benchto.driver.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpServerErrorException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class GraphiteClientRetryTest
        extends IntegrationTest
{
    @Autowired
    private GraphiteClient graphiteClient;

    @Test
    public void testRetries_successful()
    {
        restServiceServer.expect(requestTo("http://graphite:18088/events/")).andRespond(withServerError()); // 1 attempt fail
        restServiceServer.expect(requestTo("http://graphite:18088/events/")).andRespond(withServerError()); // 2 attempt fail
        restServiceServer.expect(requestTo("http://graphite:18088/events/")).andRespond(withSuccess()); // 3 attempt success

        graphiteClient.storeEvent(new GraphiteClient.GraphiteEventRequest.GraphiteEventRequestBuilder()
                .build());

        restServiceServer.verify();
    }

    @Test
    public void testRetries_exceeded()
    {
        assertThrows(HttpServerErrorException.class, () -> {
            restServiceServer.expect(requestTo("http://graphite:18088/events/")).andRespond(withServerError()); // 1 attempt fail
            restServiceServer.expect(requestTo("http://graphite:18088/events/")).andRespond(withServerError()); // 2 attempt fail
            restServiceServer.expect(requestTo("http://graphite:18088/events/")).andRespond(withServerError()); // 3 attempt fail

            graphiteClient.storeEvent(new GraphiteClient.GraphiteEventRequest.GraphiteEventRequestBuilder()
                    .build());
        });
    }
}
