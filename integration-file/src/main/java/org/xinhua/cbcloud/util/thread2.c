#include <pthread.h>
#include <stdio.h>
#include "org_xinhua_cbcloud_util_ThreadTest.h"//记得导入刚刚编译的那个.h文件
pthread_t pid;
void* thread_entity(void* arg)
{   
    while(1){
		usleep(100);
    	printf("I am new Thread\n");
    }
}

//这个方法要参考.h文件的15行代码，这里的参数得注意，你写死就行，不用明白为什么
JNIEXPORT void JNICALL Java_org_xinhua_cbcloud_util_ThreadTest_start0
(JNIEnv *env, jobject c1){
	pthread_create(&pid,NULL,thread_entity,NULL);
    while(1){
		usleep(100);
		printf("I am  main\n");
	}	
}

int main()
{
    return 0;
}