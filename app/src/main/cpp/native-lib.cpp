#include <jni.h>
#include <string>
#include<stdio.h>
#include<iostream>

using namespace std;

void accessField(JNIEnv *pEnv, jobject pJobject);
void accessMethod(JNIEnv *pEnv, jobject pJobject);
void testPoint(JNIEnv *pEnv, jobject pJobject);

void crash() {
    int i = 10;
    int j = 0;
    int k = i / j;
}

extern "C" JNIEXPORT void JNICALL
Java_com_lanshifu_baselibraryktx_native_NativeClass_native_1crash(JNIEnv *env, jobject clazz) {
    std::string hello = "hello";
    crash();
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_lanshifu_baselibraryktx_native_NativeClass_createKey(
        JNIEnv *env,
        jobject clazz) {
    std::string hello = "lizhifm183gameun";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_lanshifu_baselibraryktx_native_NativeClass_crash(JNIEnv *env, jobject thiz) {


}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_lanshifu_baselibraryktx_native_NativeClass_native_1getString(JNIEnv *env, jobject thiz) {

    accessField(env, thiz);
    accessMethod(env, thiz);
    testPoint(env, thiz);
    return env->NewStringUTF("hello");
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_lanshifu_baselibraryktx_native_NativeClass_appendString(JNIEnv *env, jobject thiz,
                                                                 jstring s1, jstring s2) {
    //使用对应的 JNI 函数把 jstring 转成 C/C++字串
    //Unicode 以 16-bits 值编码；UTF-8 是一种以字节为单位变长格式的字符编码，并与 7-bits
    //ASCII 码兼容。UTF-8 字串与 C 字串一样，以 NULL('\0')做结束符
    //调用 GetStringUTFChars，把一个 Unicode 字串转成 UTF-8 格式字串

    const char *string1 = env->GetStringUTFChars(s1, NULL);
    const char *string2 = env->GetStringUTFChars(s2, NULL);
    //string：string是STL当中的一个容器，对其进行了封装，所以操作起来非常方便。
    //char*：char *是一个指针，可以指向一个字符串数组，至于这个数组可以在栈上分配，也可以在堆上分配，堆得话就要你手动释放了。
    std::string const result = std::string(string1) + std::string(string2);
    env->ReleaseStringUTFChars(s1, string1);
    env->ReleaseStringUTFChars(s2, string2);

    return env->NewStringUTF(result.c_str());
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_lanshifu_baselibraryktx_native_NativeClass_sumArray(JNIEnv *env, jobject thiz,
                                                             jintArray int_array) {
    jint sum = 0;
    jsize length = env->GetArrayLength(int_array);

    //通过GetIntArrayElements和ReleaseIntArrayElements，
    jint *intArrayElements = env->GetIntArrayElements(int_array, NULL);
    for (jint i = 0; i < length; ++i) {
        sum += intArrayElements[i];
    }
    env->ReleaseIntArrayElements(int_array, intArrayElements, 0);
    return sum;

}


/**
 * 修改java层属性

 访问对象成员分三步，
 1. 通过 GetObjectClass 从 obj 对象得到 cls.
 2. 通过 GetFieldID 得到对象成员 ID, 如下：
 fid = (*env)->GetFieldID(env, cls, "s", "Ljava/lang/String;");
 3. 通过在对象上调用下述方法获得成员的值：
 jstr = (*env)->GetObjectField(env, obj, fid);
 此外 JNI 还提供Get/SetIntField，Get/SetFloatField 访问不同类型成员。
 */
void accessField(JNIEnv *env, jobject obj){

    //1、获取class
    jclass jClass = env->GetObjectClass(obj);
    //2. 通过GetFieldID获取fieldid
    // 签名一定要熟悉，否则在运行时直接会导致崩溃。比如String对应签名时"Ljava/lang/String;"
    jfieldID fieldId = env->GetFieldID(jClass, "value", "Ljava/lang/String;");
    jstring js = static_cast<jstring>(env->GetObjectField(obj,fieldId));
    js = env->NewStringUTF("newValueByNative");
    jstring jsStatic = env->NewStringUTF("newStaticValueByNative");
    env->SetObjectField(obj, fieldId,js);

//    jfieldID staticFieldId = env->GetStaticFieldID(jClass, "value_static", "Ljava/lang/String;");
//    env->SetStaticObjectField(jClass, staticFieldId,jsStatic);
}

void accessMethod(JNIEnv *env, jobject obj) {
    jclass objectClass = env->GetObjectClass(obj);
    jmethodID methodId = env->GetMethodID(objectClass, "setValue", "(Ljava/lang/String;)V");
    if (methodId == NULL){
        return;
    }
    jstring js = env->NewStringUTF("accessMethodNative");
    env->CallVoidMethod(obj,methodId,js);
}

void testPoint(JNIEnv *env, jobject obj) {
    int a = 1;
    int *p = &a;
    *p = 10;
    cout<< p << endl;


}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_lanshifu_oommonitor_OOMMonitor_appendString(JNIEnv *env, jobject thiz, jstring s1,
                                                     jstring s2) {

}