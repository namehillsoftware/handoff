package com.namehillsoftware.handoff.errors.GivenAStackTrace.AndStackTraceFilteringIsEnabled.AndItContainsThreeHandoffElements;

import com.namehillsoftware.handoff.StackTraceFilteredCondition;
import com.namehillsoftware.handoff.errors.HandoffStackTraceFiltering;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenFilteringTheStackTrace {
	private static Throwable filteredException;

	@BeforeClass
	public static void act() {
		HandoffStackTraceFiltering.toggleStackTraceFiltering(true);
		final Throwable exception = new Throwable("error");
		final StackTraceElement[] stackTraceElements = new StackTraceElement[3];
		stackTraceElements[0] = new StackTraceElement(
			StackTraceFilteredCondition.class.getName(),
			"testMethod",
			"Test.java",
			280);
		stackTraceElements[1] = new StackTraceElement(
			WhenFilteringTheStackTrace.class.getName(),
			"testMethodTwo",
			"QURPG9Ag.java",
			201);
		stackTraceElements[2] = new StackTraceElement(
			WhenFilteringTheStackTrace.class.getName(),
			"testMethodTwo",
			"QURPG9Ag.java",
			201);
		exception.setStackTrace(stackTraceElements);
		HandoffStackTraceFiltering.filterStackTrace(exception);
		filteredException = exception;
		HandoffStackTraceFiltering.toggleStackTraceFiltering(false);
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
				"1 handoff frames omitted from stack trace",
				"",
				"",
				0),
			new StackTraceElement(
				WhenFilteringTheStackTrace.class.getName(),
				"testMethodTwo",
				"QURPG9Ag.java",
				201)
		);
	}
}
