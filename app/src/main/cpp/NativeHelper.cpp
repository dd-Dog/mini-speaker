//
// Created by bian on 2021/2/3.
//
#include "com_flyscale_alertor_jni_NativeHelper.h";
#include <android/log.h>
#include <string.h>
#include "des.h"

#define TAG "NativeHelper-JNI" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型
/*
 * Class:     com_flyscale_alertor_jni_NativeHelper
 * Method:    stringFromJNI
 * Signature: ()Ljava/lang/String;
 */
extern "C" JNIEXPORT jstring JNICALL Java_com_flyscale_alertor_jni_NativeHelper_stringFromJNI
  (JNIEnv *env, jclass jclazz){
    return env->NewStringUTF("hello world from cpp");
  }

jbyteArray ConvertCharsToJByteArray(JNIEnv *env, unsigned char* chars, int length){
    jbyte jbytes[length];
    memset(&jbytes,0,length);
    memcpy(&jbytes,chars,length);
    //below is the return 's bytearray lens
    jbyteArray jarray = env->NewByteArray(length);
    env->SetByteArrayRegion(jarray, 0, length,jbytes);
    return jarray;
}
/**
*jbytearray转char*，直接拷贝
*/
unsigned char* ConvertJByteArrayToChars(JNIEnv *env, jbyteArray bytearray)
  {
      LOGD("ConvertJByteArrayToChars");
      unsigned char *chars = NULL;
      jbyte *bytes;
      bytes = env->GetByteArrayElements(bytearray, 0);

      int chars_len = env->GetArrayLength(bytearray);
      chars = new unsigned char[chars_len];
      memset(chars,0,chars_len);
      memcpy(chars, bytes, chars_len);
      /*
      LOGD("打印jbyte数据开始");
      for(int i=0; i<chars_len;i++){
        LOGD("%x", bytes[i]);
      }
      LOGD("打印jbyte数据结束");*/
      LOGD("长度为：%d", chars_len);
      for(int i=0; i<chars_len; i++){
        LOGD("%x", chars[i]);
      }
      env->ReleaseByteArrayElements(bytearray, bytes, 0);
      return chars;
  }

/*
 * Class:     com_flyscale_alertor_jni_NativeHelper
 * Method:    desEncrypt
 * Signature: ([B[B)[B
 */

extern "C" JNIEXPORT jbyteArray JNICALL Java_com_flyscale_alertor_jni_NativeHelper_desEncrypt
  (JNIEnv *env, jclass jclazz, jbyteArray key_, jbyteArray data_)
  {
        LOGD("desEncrypt");
        int length = 56;
        unsigned char *key = ConvertJByteArrayToChars(env, key_);
        unsigned char *data = ConvertJByteArrayToChars(env, data_);
        unsigned char *out = new unsigned char[length];
        memset((void*)out, 0x00, length);
        test_Enc48(data, key, out);
        LOGD("加密结果：");
        for(int i=0; i<length; i++){
            LOGD("%x ", out[i]);
        }
        jbyteArray result = ConvertCharsToJByteArray(env, out, length);
        return result;
  }

/*
 * Class:     com_flyscale_alertor_jni_NativeHelper
 * Method:    desDecrypt
 * Signature: ([B[B)[B
 */
extern "C" JNIEXPORT jbyteArray JNICALL Java_com_flyscale_alertor_jni_NativeHelper_desDecrypt
  (JNIEnv *env, jclass jclazz, jbyteArray key, jbyteArray encryptedData){

  }

