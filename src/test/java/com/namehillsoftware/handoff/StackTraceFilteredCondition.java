package com.namehillsoftware.handoff;

import org.assertj.core.api.Condition;

public class StackTraceFilteredCondition extends Condition<StackTraceElement> {
	@Override
	public boolean matches(StackTraceElement value) {
		return value.getClassName().startsWith(getClass().getPackage().getName());
	}
}
