package org.quatrix.automation;

import org.junit.Rule;
import org.junit.rules.Timeout;
import org.mule.modules.tests.ConnectorTestCase;

import java.util.concurrent.TimeUnit;

public class QuatrixParentTestCase extends ConnectorTestCase {

    private static final Delay SETUP_DELAY = new Delay(TimeUnit.SECONDS, 5L);

    @Rule
    public Timeout globalTimeout = new Timeout(10, TimeUnit.MINUTES);


    @Override
    protected <T> T runFlowAndGetPayload(String flowName) throws Exception {
        T payload = super.runFlowAndGetPayload(flowName);
        SETUP_DELAY.exec();
        return payload;
    }

    @Override
    protected <T> T runFlowAndGetPayload(String flowName, String beanId) throws Exception {
        T payload = super.runFlowAndGetPayload(flowName, beanId);
        SETUP_DELAY.exec();

        return payload;
    }

    private static class Delay {
        private TimeUnit timeUnit;
        private long amount;

        public Delay(TimeUnit timeUnit, long amount) {
            this.timeUnit = timeUnit;
            this.amount = amount;
        }

        void exec() throws InterruptedException {
            timeUnit.sleep(amount);
        }
    }
}
