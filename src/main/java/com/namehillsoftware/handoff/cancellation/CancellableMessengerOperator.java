package com.namehillsoftware.handoff.cancellation;

import com.namehillsoftware.handoff.promises.MessengerOperator;

public interface CancellableMessengerOperator<Resolution> extends MessengerOperator<Resolution>, CancellationResponse {
}
