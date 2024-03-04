package com.namehillsoftware.handoff.promises.GivenAPromiseThatIsRejected.AndContinuingWithResponseAndRejection.AndTheRejectionThrowsAnError;

import com.namehillsoftware.handoff.StackTraceFilteredCondition;
import com.namehillsoftware.handoff.promises.Promise;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by david on 10/17/16.
 */
public class WhenAnotherReturningPromiseIsExpected {

	private Integer nextReturningPromiseResult;
	private static final Exception thrownException = new Exception();
	private static Throwable caughtException;
	private Throwable finalException;

	@Before
	public void before() {
		Promise.Rejections.toggleStackTraceFiltering(true);
		new Promise<>(thrownException)
				.then(result -> 330 + result.hashCode(), err -> {
					caughtException = err;
					throw new Exception();
				})
				.then(nextResult -> nextReturningPromiseResult = nextResult)
				.excuse(err -> {
					finalException = err;
					return null;
				})
				.must(() -> Promise.Rejections.toggleStackTraceFiltering(false));
	}

	@Test
	public void thenTheRejectionStackIsCorrect() {
		assertThat(finalException.getStackTrace()).has(new StackTraceFilteredCondition());
	}

	@Test
	public void thenTheRejectionIsCorrect() {
		assertThat(caughtException).isEqualTo(thrownException);
	}

	@Test
	public void thenTheNextActionIsNotCalled() {
		assertThat(nextReturningPromiseResult).isNull();
	}
}
