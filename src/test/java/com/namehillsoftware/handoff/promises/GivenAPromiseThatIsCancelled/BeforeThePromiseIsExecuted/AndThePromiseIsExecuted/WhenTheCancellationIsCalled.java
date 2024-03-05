package com.namehillsoftware.handoff.promises.GivenAPromiseThatIsCancelled.BeforeThePromiseIsExecuted.AndThePromiseIsExecuted;

import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.promises.MessengerOperator;
import com.namehillsoftware.handoff.promises.Promise;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenTheCancellationIsCalled {

	private static final Throwable thrownException = new Exception();
	private static Throwable caughtException;

	@BeforeClass
	public static void before() {
		final ExternallyResolvableOperator<String> resolvableTask = new ExternallyResolvableOperator<>();
		final Promise<String> promise = new Promise<>(resolvableTask);

		final Promise<Object> cancellablePromise = promise.eventually((result) -> new Promise<>((messenger) ->{}, (messenger -> messenger.sendRejection(thrownException))));

		cancellablePromise.excuse((exception) -> caughtException = exception);

		cancellablePromise.cancel();

		resolvableTask.resolve("");
	}

	@Test
	public void thenTheRejectionIsCorrect() {
		assertThat(caughtException).isEqualTo(thrownException);
	}

	private static class ExternallyResolvableOperator<TResult> implements MessengerOperator<TResult> {

		private Messenger<TResult> resolve;

		void resolve(TResult resolution) {
			if (resolve != null)
				resolve.sendResolution(resolution);
		}

		@Override
		public void send(Messenger<TResult> messenger) {
			resolve = messenger;
		}
	}
}
