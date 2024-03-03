package com.namehillsoftware.handoff.promises.GivenAPromiseThatResolves.AndContinuingWithResponseAndRejection.AndTheRejectionThrowsAnError;

import com.namehillsoftware.handoff.promises.Promise;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by david on 10/17/16.
 */
public class WhenAnotherReturningPromiseIsExpected {

	private static Integer nextReturningPromiseResult;
	private static boolean isErrorCalled;

	private static boolean isCancelled;

	private static Throwable caughtException;

	@BeforeClass
	public static void before() {
		new Promise<>("test")
				.<Integer>then((result, cancellationSignal) -> {
					isCancelled = cancellationSignal.isCancelled();
					nextReturningPromiseResult = 330 + result.hashCode();
					throw new Exception();
				}, (err, cancellationSignal) -> {
					caughtException = err;
					throw new Exception();
				})
				.then(nextResult -> nextReturningPromiseResult = nextResult)
				.excuse(err -> isErrorCalled = true);
	}

	@Test
	public void thenTheResponseIsNotCancelled() {
		assertThat(isCancelled).isFalse();
	}

	@Test
	public void thenTheRejectionIsNotCalled() {
		assertThat(caughtException).isNull();
	}

	@Test
	public void thenTheNextActionIsCalled() {
		assertThat(nextReturningPromiseResult).isEqualTo(330 + "test".hashCode());
	}

	@Test
	public void thenTheErrorIsCalled() {
		assertThat(isErrorCalled).isTrue();
	}
}
