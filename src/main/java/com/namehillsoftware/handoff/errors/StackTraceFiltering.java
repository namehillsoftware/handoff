package com.namehillsoftware.handoff.errors;

import com.namehillsoftware.handoff.Messenger;

import java.util.ArrayList;

public final class StackTraceFiltering {

	// Use a module in the root namespace
	private static final String packageName = Messenger.class.getPackage().getName();
	private static final String omittedStackTrace = " handoff frames omitted from stack trace";

	private static boolean isStackTraceFilteringEnabled;

	public static void toggleStackTraceFiltering(boolean enable) {
		isStackTraceFilteringEnabled = enable;
	}

	public static void filterStackTrace(Throwable error) {
		if (!isStackTraceFilteringEnabled) return;

		final StackTraceElement[] stackTraceElements = error.getStackTrace();
		final ArrayList<StackTraceElement> reducedStackTraceElements = new ArrayList<>(stackTraceElements.length);

		StackTraceElement firstOmittedStackTraceElement = null;
		StackTraceElement lastOmittedStackTraceElement = null;
		int omittedStackTraceElementCount = 0;
		for (StackTraceElement element : stackTraceElements) {
			if (!element.getClassName().startsWith(packageName)) {
				fillInOmittedElements(firstOmittedStackTraceElement, lastOmittedStackTraceElement, omittedStackTraceElementCount, reducedStackTraceElements);
				firstOmittedStackTraceElement = null;
				lastOmittedStackTraceElement = null;
				omittedStackTraceElementCount = 0;

				reducedStackTraceElements.add(element);
				continue;
			}

			++omittedStackTraceElementCount;

			if (firstOmittedStackTraceElement == null) {
				firstOmittedStackTraceElement = element;
				continue;
			}

			lastOmittedStackTraceElement = element;
		}

		fillInOmittedElements(firstOmittedStackTraceElement, lastOmittedStackTraceElement, omittedStackTraceElementCount, reducedStackTraceElements);

		final StackTraceElement[] newStackTrace = new StackTraceElement[reducedStackTraceElements.size()];
		error.setStackTrace(reducedStackTraceElements.toArray(newStackTrace));
	}

	private static void fillInOmittedElements(StackTraceElement firstOmittedStackTraceElement, StackTraceElement lastOmittedStackTraceElement, int omittedStackTraceElementsSize, ArrayList<StackTraceElement> reducedStackTraceElements) {
		if (firstOmittedStackTraceElement != null)
			reducedStackTraceElements.add(firstOmittedStackTraceElement);

		if (omittedStackTraceElementsSize > 2) {
			reducedStackTraceElements.add(
				new StackTraceElement(
					omittedStackTraceElementsSize - 2 + omittedStackTrace,
					"",
					"",
					0));
		}

		if (lastOmittedStackTraceElement != null)
			reducedStackTraceElements.add(lastOmittedStackTraceElement);
	}
}
