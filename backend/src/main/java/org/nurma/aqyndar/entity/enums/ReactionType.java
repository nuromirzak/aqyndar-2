package org.nurma.aqyndar.entity.enums;

public enum ReactionType {
    LIKE(1), NONE(0), DISLIKE(-1);

    private final int value;

    ReactionType(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ReactionType fromValue(final int value) {
        for (ReactionType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid value for ReactionType: " + value);
    }
}

