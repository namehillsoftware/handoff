package com.namehillsoftware.handoff.promises.GivenAProxiedPromiseThatIsCancelled.AndTheCancellationCreatesAnException;

import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.propagation.PromiseProxy;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by david on 10/17/16.
 */

public class WhenTheCancellationIsCalled {

	private static final Messenger<Object> messenger = new Messenger<Object>() {
		@Override
		public void sendResolution(Object tResult) {}

		@Override
		public void sendRejection(Throwable error) {}

		@Override
		public Promise<Void> promisedCancellation() {
			return Promise.empty();
		}
	};
	private static boolean unhandledRejection;

	@BeforeClass
	public static void before() throws Throwable {
		Promise.Rejections.setUnhandledRejectionsReceiver(rejection -> unhandledRejection = true);
		final Promise<Object> promisedObject = new Promise<>((m) -> m.promisedCancellation().then(c -> {
			m.sendRejection(new Exception());
			return null;
		}));
		final PromiseProxy<Object> proxy = new PromiseProxy<>(messenger);
		proxy.proxy(promisedObject);
	}

	@Test
	public void thenTheExceptionIsHandled() {
		assertThat(unhandledRejection).isFalse();
	}
}
