package com.namehillsoftware.handoff.errors;

import com.namehillsoftware.handoff.Messenger;

import java.util.ArrayList;

public final class StackTraceFiltering {

	// Use a module in the root namespace
	private static final String packageName = Messenger.class.getPackage().getName();
	private static final String omittedStackTrace = "handoff frames omitted from stack trace";

	private static boolean isStackTraceFilteringEnabled;

	public static void toggleStackTraceFiltering(boolean enable) {
		isStackTraceFilteringEnabled = enable;
	}

	public static void filterStackTrace(Throwable error) {
		if (!isStackTraceFilteringEnabled) return;

		final StackTraceElement[] stackTraceElements = error.getStackTrace();
		final ArrayList<StackTraceElement> reducedStackTraceElements = new ArrayList<>(stackTraceElements.length);

		final ArrayList<StackTraceElement> omittedStackTraceElements = new ArrayList<>();
		for (StackTraceElement element : stackTraceElements) {
			if (element.getClassName().startsWith(packageName)) {
				omittedStackTraceElements.add(element);
				continue;
			}

			fillInOmittedElements(omittedStackTraceElements, reducedStackTraceElements);
			reducedStackTraceElements.add(element);
		}

		fillInOmittedElements(omittedStackTraceElements, reducedStackTraceElements);

		final StackTraceElement[] newStackTrace = new StackTraceElement[reducedStackTraceElements.size()];
		error.setStackTrace(reducedStackTraceElements.toArray(newStackTrace));
	}

	private static void fillInOmittedElements(ArrayList<StackTraceElement> omittedStackTraceElements, ArrayList<StackTraceElement> reducedStackTraceElements) {
		final int omittedStackTraceElementsSize = omittedStackTraceElements.size();
		if (omittedStackTraceElementsSize > 0) {
			reducedStackTraceElements.add(omittedStackTraceElements.get(0));

			if (omittedStackTraceElementsSize > 2) {
				reducedStackTraceElements.add(new StackTraceElement(omittedStackTrace, "", "", 0));
			}

			if (omittedStackTraceElementsSize > 1) {
				reducedStackTraceElements.add(omittedStackTraceElements.get(omittedStackTraceElementsSize - 1));
			}

			omittedStackTraceElements.clear();
		}
	}
}
