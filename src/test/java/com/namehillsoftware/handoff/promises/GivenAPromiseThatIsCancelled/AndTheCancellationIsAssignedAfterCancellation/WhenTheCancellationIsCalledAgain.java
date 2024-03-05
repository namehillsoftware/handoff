package com.namehillsoftware.handoff.promises.GivenAPromiseThatIsCancelled.AndTheCancellationIsAssignedAfterCancellation;

import com.namehillsoftware.handoff.promises.Promise;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenTheCancellationIsCalledAgain {

	private static boolean isCancelled;

	@BeforeClass
	public static void before() {
		new Promise<String>() {
			{
				cancel();

				awaitCancellation(() -> isCancelled = true);

				cancel();
			}
		};
	}

	@Test
	public void thenTheCancellationIsNotCalled() {
		assertThat(isCancelled).isFalse();
	}
}
