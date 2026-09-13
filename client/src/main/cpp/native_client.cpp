#include "native_client.h"

#include <android/log.h>
#include <string>

#define LOG_TAG "CRMPNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)

namespace {
constexpr jint kResultOk = 0;
constexpr jint kResultNotImplemented = 1;
constexpr jint kResultInvalidArgs = 2;

std::string jstringToUtf8(JNIEnv *env, jstring value) {
    if (value == nullptr) {
        return {};
    }
    const char *chars = env->GetStringUTFChars(value, nullptr);
    if (chars == nullptr) {
        return {};
    }
    std::string out(chars);
    env->ReleaseStringUTFChars(value, chars);
    return out;
}
}  // namespace

extern "C" JNIEXPORT jint JNICALL
Java_com_crmp_mobile_client_NativeClient_nativeLaunch(
    JNIEnv *env, jclass /*clazz*/, jstring server, jstring nick) {
    const std::string serverUtf8 = jstringToUtf8(env, server);
    const std::string nickUtf8 = jstringToUtf8(env, nick);

    if (serverUtf8.empty() || nickUtf8.empty()) {
        LOGW("nativeLaunch: invalid args");
        return kResultInvalidArgs;
    }

    // TODO: establish connection to SA-MP/CRMP server at serverUtf8 with nickUtf8.
    // TODO: load user-provided game data directory (never ship Rockstar assets).
    // TODO: start client main loop / rendering.
    LOGI("nativeLaunch stub: server=%s nick=%s", serverUtf8.c_str(), nickUtf8.c_str());
    (void)kResultOk;
    return kResultNotImplemented;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_crmp_mobile_client_NativeClient_nativeGetVersion(
    JNIEnv *env, jclass /*clazz*/) {
    return env->NewStringUTF("crmp-client-stub/0.1.0");
}
