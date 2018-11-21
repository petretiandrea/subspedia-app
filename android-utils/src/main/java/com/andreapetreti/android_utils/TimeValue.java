package com.andreapetreti.android_utils;

import java.util.concurrent.TimeUnit;

public class TimeValue {

    private final long mValue;
    private final TimeUnit mTimeUnit;

    public static TimeValue fromDays(long value) {
        return of(value, TimeUnit.DAYS);
    }

    public static TimeValue fromMinutes(long value) {
        return of(value, TimeUnit.MINUTES);
    }

    public static TimeValue fromMicroseconds(long value) {
        return of(value, TimeUnit.MICROSECONDS);
    }

    public static TimeValue fromMilliseconds(long value) {
        return of(value, TimeUnit.MILLISECONDS);
    }

    public static TimeValue fromNanoseconds(long value) {
        return of(value, TimeUnit.NANOSECONDS);
    }

    public static TimeValue fromHours(long value) {
        return of(value, TimeUnit.HOURS);
    }

    public static TimeValue of(long value, TimeUnit timeUnit) {
        return new TimeValue(value, timeUnit);
    }

    private TimeValue(long value, TimeUnit timeUnit) {
        mValue = value;
        mTimeUnit = timeUnit;
    }

    public long getValue() {
        return mValue;
    }

    public TimeUnit getTimeUnit() {
        return mTimeUnit;
    }

    public long convert(TimeUnit destination) {
        return destination.convert(getValue(), getTimeUnit());
    }
}
