package org.hyperskill.secretdiary.internals

import android.os.SystemClock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@Implements(Clock.System::class)
class CustomClockSystemShadow {

    @Implementation
    fun now(): Instant {
        return SystemClock.currentGnssTimeClock().instant().toKotlinInstant()
    }
}