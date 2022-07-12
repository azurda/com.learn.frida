#include <android/log.h>
#include <jni.h>
#include <sys/stat.h>
#include <stdio.h>
#include <string.h>

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_learn_frida_MainActivity_isFridaRunning(JNIEnv *env, jobject thiz) {
    int result;
    struct stat sb;
    if (stat("/data/local/tmp/frida-server", &sb) == 0) {
        return true;
    }

    return false;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_learn_frida_MainActivity_isFridaProc(JNIEnv *env, jobject thiz) {
    // TODO: implement isFridaProc()

    FILE *fp;
    char line[512] = {0};
    fp = fopen("/proc/self/maps", "r");
    if (fp) {
        while (fgets(line, 512, fp)) {
            if (strstr(line, "frida-agent")) {
                return true;
            }
        }
    }
    return false;
}
