package com.mine.bicycle.annotation

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ClockAnnotation

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BicycleAnnotation

