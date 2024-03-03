package com.namehillsoftware.handoff.promises.GivenAPromiseThatIsCancelled.AndTheCancellationIsAssignedAfterCancellation;

import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.promises.MessengerOperator;
import com.namehillsoftware.handoff.promises.Promise;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenTheCancellationIsCalledAgain {

	private static int cancellationCalls;

	@BeforeClass
	public static void before() {
		final MessengerExposingOperator messengerOperator = new MessengerExposingOperator();
		final Promise<String> cancellablePromise = new Promise<>(messengerOperator);

		cancellablePromise.cancel();

		messengerOperator.messenger.promisedCancellation().must(() -> cancellationCalls++);

		cancellablePromise.cancel();
	}

	@Test
	public void thenTheCancellationIsNotCalledTwice() {
		assertThat(cancellationCalls).isEqualTo(1);
	}

	private static class MessengerExposingOperator implements MessengerOperator<String> {
		public Messenger<String> messenger;

		@Override
		public void send(Messenger<String> messenger) {
			this.messenger = messenger;
		}
	};
}
