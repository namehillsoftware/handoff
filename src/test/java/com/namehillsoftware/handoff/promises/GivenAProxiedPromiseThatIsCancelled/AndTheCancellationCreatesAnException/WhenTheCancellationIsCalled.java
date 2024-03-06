package com.namehillsoftware.handoff.promises.GivenAProxiedPromiseThatIsCancelled.AndTheCancellationCreatesAnException;

import com.namehillsoftware.handoff.promises.Promise;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by david on 10/17/16.
 */

public class WhenTheCancellationIsCalled {

	private static boolean unhandledRejection;

	@BeforeClass
	public static void before() throws Throwable {
		Promise.Rejections.setUnhandledRejectionsReceiver(rejection -> unhandledRejection = true);
		final Promise<Object> promisedObject = new Promise<>((m) -> {});
		final Promise.Proxy<Object> proxy = new Promise.Proxy<Object>() {
			{
				proxy(promisedObject);
			}
		};

		proxy.cancel();
	}

	@Test
	public void thenTheExceptionIsHandled() {
		assertThat(unhandledRejection).isFalse();
	}
}
