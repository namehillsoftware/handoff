package com.namehillsoftware.handoff.errors.GivenAStackTrace.AndStackTraceFilteringIsEnabled.AndItContainsMoreThanThreeHandoffElements;

import com.namehillsoftware.handoff.StackTraceFilteredCondition;
import com.namehillsoftware.handoff.errors.StackTraceFiltering;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenFilteringTheStackTrace {
	private static Throwable filteredException;

	@BeforeClass
	public static void act() {
		StackTraceFiltering.toggleStackTraceFiltering(true);
		final Throwable exception = new Throwable("error");
		final StackTraceElement[] stackTraceElements = new StackTraceElement[4];
		stackTraceElements[0] = new StackTraceElement(
			StackTraceFilteredCondition.class.getName(),
			"testMethod",
			"Test.java",
			280);
		stackTraceElements[1] = new StackTraceElement(
			WhenFilteringTheStackTrace.class.getName(),
			"testMethodTwo",
			"QURPG9Ag.java",
			801);
		stackTraceElements[2] = new StackTraceElement(
			WhenFilteringTheStackTrace.class.getName(),
			"testMethodTwo",
			"QURPG9Ag.java",
			584);
		stackTraceElements[3] = new StackTraceElement(
			WhenFilteringTheStackTrace.class.getName(),
			"p3YPFqrn0Q",
			"qtEzfCbrO.java",
			257);
		exception.setStackTrace(stackTraceElements);
		StackTraceFiltering.filterStackTrace(exception);
		filteredException = exception;
		StackTraceFiltering.toggleStackTraceFiltering(false);
	}

	@Test
	public void thenTheStackTraceIsCorrect() {
		assertThat(filteredException.getStackTrace()).containsExactly(
			new StackTraceElement(
				StackTraceFilteredCondition.class.getName(),
				"testMethod",
				"Test.java",
				280),
			new StackTraceElement(
				"2 handoff frames omitted from stack trace",
				"",
				"",
				0),
			new StackTraceElement(
				WhenFilteringTheStackTrace.class.getName(),
				"p3YPFqrn0Q",
				"qtEzfCbrO.java",
				257)
		);
	}
}
