package com.namehillsoftware.handoff.promises.GivenAPromiseThatIsRejected;

import com.namehillsoftware.handoff.StackTraceFilteredCondition;
import com.namehillsoftware.handoff.errors.StackTraceFiltering;
import com.namehillsoftware.handoff.promises.Promise;
import org.assertj.core.api.Condition;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class WhenTheRejectionIsCalled {

	private static final Exception thrownException = new Exception();
	private static Throwable caughtException;

	@BeforeClass
	public static void before() {
		new Promise<String>(thrownException).excuse(exception -> caughtException = exception);
	}

	@Test
	public void thenTheRejectionIsCorrect() {
		assertThat(caughtException).isEqualTo(thrownException);
	}
}
