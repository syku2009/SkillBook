package com.skillbook.data;

/**
 * 키 입력 타입 정의
 */
public enum KeyType {
    RIGHT_CLICK("우클릭"),
    LEFT_CLICK("좌클릭"),
    SHIFT_RIGHT_CLICK("쉬프트+우클릭"),
    SHIFT_LEFT_CLICK("쉬프트+좌클릭"),
    DOUBLE_SHIFT("쉬프트 두 번");

    private final String displayName;

    KeyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
