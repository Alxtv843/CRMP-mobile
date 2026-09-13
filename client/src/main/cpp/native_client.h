#pragma once

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

/**
 * Clean-room native client stubs for CRMP / SA-MP Mobile.
 *
 * TODO:
 *  - Parse host:port, resolve DNS
 *  - Implement SA-MP / CRMP query + join handshake (public protocol docs only)
 *  - Integrate renderer / audio once legal game data path is configured by the user
 *  - Never embed GTA SA assets or proprietary client binaries
 */

JNIEXPORT jint JNICALL
Java_com_crmp_mobile_client_NativeClient_nativeLaunch(
    JNIEnv *env, jclass clazz, jstring server, jstring nick);

JNIEXPORT jstring JNICALL
Java_com_crmp_mobile_client_NativeClient_nativeGetVersion(
    JNIEnv *env, jclass clazz);

#ifdef __cplusplus
}
#endif
