package com.namehillsoftware.handoff.errors.GivenAStackTrace.AndStackTraceFilteringIsEnabled.AndItContainsMoreThanThreeHandoffElements;

import com.namehillsoftware.handoff.StackTraceFilteredCondition;
import com.namehillsoftware.handoff.errors.HandoffStackTraceFiltering;
import com.namehillsoftware.handoff.promises.Promise;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenFilteringTheStackTrace {
	private static Throwable filteredException;

	@BeforeClass
	public static void act() {
		HandoffStackTraceFiltering.toggleStackTraceFiltering(true);
		final Throwable exception = new Throwable("error");
		final ArrayList<StackTraceElement> stackTraceElements = new ArrayList<>();
		stackTraceElements.add(new StackTraceElement(
			StackTraceFilteredCondition.class.getName(),
			"testMethodZero",
			"fj4Y9RdC.java",
			342));
		stackTraceElements.add(new StackTraceElement(
			String.class.getName(),
			"stringMethod",
			"String.java",
			100));
		stackTraceElements.add(new StackTraceElement(
			StackTraceFilteredCondition.class.getName(),
			"testMethod",
			"Test.java",
			280));
		stackTraceElements.add(new StackTraceElement(
			WhenFilteringTheStackTrace.class.getName(),
			"testMethodTwo",
			"QURPG9Ag.java",
			801));
		stackTraceElements.add(new StackTraceElement(
			WhenFilteringTheStackTrace.class.getName(),
			"testMethodTwo",
			"QURPG9Ag.java",
			584));
		stackTraceElements.add(new StackTraceElement(
			WhenFilteringTheStackTrace.class.getName(),
			"p3YPFqrn0Q",
			"qtEzfCbrO.java",
			257));
		stackTraceElements.add(new StackTraceElement(
			Integer.class.getName(),
			"Eleifendsodales",
			"Laciniapulvinar.java",
			132));
		stackTraceElements.add(new StackTraceElement(
			Long.class.getName(),
			"Liberoaugue",
			"Sociosqumolestie.java",
			435));
		stackTraceElements.add(new StackTraceElement(
			WhenFilteringTheStackTrace.class.getName(),
			"testMethodFifty",
			"b8.java",
			133));
		stackTraceElements.add(new StackTraceElement(
			Promise.class.getName(),
			"then",
			"Promie.java",
			191));
		stackTraceElements.add(new StackTraceElement(
			ArrayList.class.getName(),
			"add",
			"StdLib.java",
			346));

		final StackTraceElement[] stackTrace = new StackTraceElement[stackTraceElements.size()];
		exception.setStackTrace(stackTraceElements.toArray(stackTrace));
		HandoffStackTraceFiltering.filterStackTrace(exception);
		filteredException = exception;
		HandoffStackTraceFiltering.toggleStackTraceFiltering(false);
	}

	@Test
	public void thenTheStackTraceIsCorrect() {
		assertThat(filteredException.getStackTrace()).containsExactly(
			new StackTraceElement(
				StackTraceFilteredCondition.class.getName(),
				"testMethodZero",
				"fj4Y9RdC.java",
				342),
			new StackTraceElement(
				String.class.getName(),
				"stringMethod",
				"String.java",
				100),
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
				257),
			new StackTraceElement(
				Integer.class.getName(),
				"Eleifendsodales",
				"Laciniapulvinar.java",
				132),
			new StackTraceElement(
				Long.class.getName(),
				"Liberoaugue",
				"Sociosqumolestie.java",
				435),
			new StackTraceElement(
				WhenFilteringTheStackTrace.class.getName(),
				"testMethodFifty",
				"b8.java",
				133),
			new StackTraceElement(
				Promise.class.getName(),
				"then",
				"Promie.java",
				191),
			new StackTraceElement(
				ArrayList.class.getName(),
				"add",
				"StdLib.java",
				346)
		);
	}
}
