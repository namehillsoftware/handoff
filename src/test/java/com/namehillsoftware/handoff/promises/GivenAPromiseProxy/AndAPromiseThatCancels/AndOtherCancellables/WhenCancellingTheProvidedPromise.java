package com.namehillsoftware.handoff.promises.GivenAPromiseProxy.AndAPromiseThatCancels.AndOtherCancellables;

import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.propagation.ProxyPromise;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenCancellingTheProvidedPromise {

	private static int Cancellations;

	private static Throwable Error;

	@BeforeClass
	public static void act() {
		final ProxyPromise<Object> proxyPromise = new ProxyPromise<>(
			cancellationProxy -> {
				cancellationProxy.doCancel(() -> Cancellations++);
				cancellationProxy.doCancel(() -> Cancellations++);
				cancellationProxy.doCancel(() -> Cancellations++);
				return new Promise<>((m) -> m.awaitCancellation(() -> m.sendRejection(new Exception("Cancelled!"))));
			});
		proxyPromise.excuse(r -> Error = r);
		proxyPromise.cancel();
	}

	@Test
	public void thenTheExceptionIsCorrect() {
		assertThat(Error.getMessage()).isEqualTo("Cancelled!");
	}

	@Test
	public void thenTheOtherCancellablesAreCancelled() {
		assertThat(Cancellations).isEqualTo(3);
	}
}
