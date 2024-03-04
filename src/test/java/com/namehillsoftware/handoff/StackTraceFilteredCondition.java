package com.namehillsoftware.handoff;

import org.assertj.core.api.Condition;

public class StackTraceFilteredCondition extends Condition<StackTraceElement[]> {

	private static final String rootPackageName = StackTraceFilteredCondition.class.getPackage().getName();

	@Override
	public boolean matches(StackTraceElement[] values) {
		int filteredCount = 0;
		for (StackTraceElement value : values) {
			if (!value.getClassName().startsWith(rootPackageName)) {
				filteredCount = 0;
				continue;
			}

			++filteredCount;
			if (filteredCount > 2) return false;
		}
		return true;
	}
}
