package com.solofunds.memberaccounting.messaging.messenger;

import java.io.Serializable;

public enum ResponseStatus implements Serializable {
    OK,
    NOT_FOUND,
    INTERNAL_ERROR
}
