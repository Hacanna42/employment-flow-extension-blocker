package io.github.hacanna42.extensionblocker.common.lock;

public enum AdvisoryLockNamespace {

    BLOCKED_EXTENSION(1);
    // 새 기능이 pg_advisory_xact_lock을 쓰게 되면 여기에 서로 다른 번호로 한 줄 추가

    private final int classId;

    AdvisoryLockNamespace(int classId) {
        this.classId = classId;
    }

    public int classId() {
        return classId;
    }
}
