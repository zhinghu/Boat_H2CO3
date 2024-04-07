#include "h2co3Launcher_internal.h"
#include <android/native_window_jni.h>
#include <jni.h>
#include <stdlib.h>

void deleteGlobalRef(JNIEnv *env, jclass *globalRef);

void deleteGlobalRef(JNIEnv *env, jclass *globalRef) {
    if (*globalRef != NULL) {
        (*env)->DeleteGlobalRef(env, *globalRef);
        *globalRef = NULL;
    }
}

void setGlobalRef(JNIEnv *env, jclass *globalRef, jclass localRef) {
    deleteGlobalRef(env, globalRef);
    *globalRef = (*env)->NewGlobalRef(env, localRef);
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherActivity_nOnCreate(JNIEnv *env, jobject thiz) {
}