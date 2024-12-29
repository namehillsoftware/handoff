package com.namehillsoftware.handoff.promises.queued;

import com.namehillsoftware.handoff.promises.queued.cancellation.CancellableMessageWriter;

public class ExecutablePromise<Resolution> extends DispatchablePromise<Resolution> implements Runnable {

    public ExecutablePromise(CancellableMessageWriter<Resolution> messageWriter) {
        super(messageWriter);
    }

    @Override
    public final void run() {
        dispatchMessage();
    }
}
