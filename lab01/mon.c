#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <signal.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <errno.h>



/* the program execution starts here */
int main(int argc, char **argv)
{
    char    *program;
    char     stringa[1000] = "";
    int num = 0;
    char snum[10];

    if (argc != 2) {
        printf("Usage: mon fileName\n where fileName is an executable file.\n");
        exit(-1);
    } else {
        program = argv[1];

    /* Here comes your code.*/
    /* It should do the following:
        1. launch the program specified by the variable 'fileName' and get its pid
        2. launch 'procmon pid' where pid is the pid of the program launched in step 1
        3. wait for 20 seconds
        4. kill the first program
        5. wait for 2 seconds
        6. kill the procmon
    */

sprintf(stringa, "./%s", program);

pid_t pid = fork();
if (pid < 0 ) {
perror("fork failed."); 
exit(1); }

else if (pid == 0) {
char* args[] = {stringa, NULL};
     execv(args[0], args);
}

else {
  
   char procmon_str[] = "./procmon";
   num = pid;
   sprintf(snum, "%d",num);
   pid_t pid2 = fork();
         if (pid2 == 0) {
         char* args2[] = {procmon_str, snum, NULL};
          execv(args2[0], args2); }
   else {
  
  printf("PID of calcloop is %s", snum);
  printf("PID of procmon is %d", pid2);

}

sleep(20);
kill(pid,SIGTERM);
sleep(2);
kill(pid2,SIGTERM);
  
}



}



return 0;

}
