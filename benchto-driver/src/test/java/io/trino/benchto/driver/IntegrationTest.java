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
package io.trino.benchto.driver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DriverApp.class, TestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE, value = "executionSequenceId=BEN_SEQ_ID")
public abstract class IntegrationTest
{
    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    protected ApplicationContext context;

    protected MockRestServiceServer restServiceServer;

    @BeforeEach
    public void resetMocks()
    {
        for (String name : context.getBeanDefinitionNames()) {
            Object bean = context.getBean(name);
            if (MockUtil.isMock(bean)) {
                Mockito.reset(bean);
            }
        }
    }

    @BeforeEach
    public void initializeRestServiceServer()
    {
        restServiceServer = MockRestServiceServer.createServer(restTemplate);
    }

    @AfterEach
    public void verifyRestServiceServer()
    {
        restServiceServer.verify();
    }
}
