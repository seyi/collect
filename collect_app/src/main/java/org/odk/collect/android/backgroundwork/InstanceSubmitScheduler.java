package org.odk.collect.android.backgroundwork;

public interface InstanceSubmitScheduler {

    void scheduleSubmitIfNeeded(String projectId);

    void scheduleSubmit(String projectId, Long instanceId);

    void cancelSubmit(String projectId);
}
