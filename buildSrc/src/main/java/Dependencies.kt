object App {
    const val application_id = "com.pj.playground"
    const val target_sdk = 29
    const val min_sdk = 21
    const val version_code = 1
    const val version_name = "1.0"
    const val build_tools = "29.0.3"
    const val junit_runner = "androidx.test.runner.AndroidJUnitRunner"
    const val source_target = 1.8
    const val jvm_target = "1.8"
}

object Versions {

    // dependencies versions
    const val kotlin = "1.3.72"
    const val appcompat = "1.1.0"
    const val core_ktx = "1.3.0"
    const val constraintlayout = "1.1.3"

    // test versions
    const val junit = "4.13"
    const val android_test = "1.1.1"
    const val espresso = "3.2.0"
}

object Deps {
    const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    const val androidx_appCompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val androidx_core_ktx = "androidx.core:core-ktx:${Versions.core_ktx}"
    const val contraintlayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintlayout}"

    // test
    const val junit = "junit:junit:${Versions.junit}"
    const val android_test = "androidx.test.ext:junit:${Versions.android_test}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
}