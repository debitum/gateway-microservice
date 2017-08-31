package com.debitum.gateway;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.function.Supplier;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GatewayApplication.class})
public abstract class IntegrationTestBase extends AbstractTransactionalJUnit4SpringContextTests {

    @PersistenceContext
    public EntityManager em;

    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(ExternalSourcesStubServer.getDefaultConfig());

    public void flush(Runnable runnable) {
        runnable.run();
        flush();
    }

    public void flush() {
        em.flush();
        em.clear();
    }

    public <T> T flushAndGet(Supplier<T> supplier) {
        T result = supplier.get();
        flush();
        return result;
    }
}


