#include<pthread.h>
#include<stdio.h>
#include "org_xinhua_cbcloud_util_ThreadTest.h"
pthread_t pid;

JNIEXPORT void JNICALL Java_org_xinhua_cbcloud_util_ThreadTest_start0(JNIEnv *env, jobject obj){
jint ret = 0;

//获取java类 
printf("init\n");
jclass cls = (*env)->FindClass(env,"org/xinhua/cbcloud/util/ThreadTest");
if(cls==NULL){
	printf("find Class error!\n");
	return;
}

//获取java类构造方法
jmethodID cid =(*env)->GetMethodID(env,cls,"<init>","()V");
if(cid==NULL){
	printf("find constructor error!\n");
	return;
}

//创建对象
jobject job = (*env)->NewObject(env,cls,cid,NULL);
if(job==NULL){
	printf("new instance error!\n");
	return;
}

//获取java的run方法
jmethodID tid =(*env)->GetMethodID(env,cls,"run","()V");
	if(tid==NULL){
	printf("find constructor error!\n");
	return;
}

//回调方法
ret = (*env)->CallIntMethod(env,job,tid,NULL);
	printf("finish JNICall!\n");
}

int main(){
	return 0;
}