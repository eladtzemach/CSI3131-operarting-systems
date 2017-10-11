#include <stdio.h>
#include <stdlib.h>
#include <string.h>
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

    	pid_t pid = fork();
    	if (pid < 0 ) {
	    perror("fork failed.");
	    exit(1); }

            //child1 - launch M
    	else if (pid == 0) { 
	    execl(program, program, NULL);
            exit(0);
    	}

    	else { //parent1
   	
            num = pid;
            int pipefd[2];
            pipe(pipefd);

            pid_t pid2 = fork();
            if (pid2 < 0 ) {
                perror("fork failed."); 
                exit(1); }
            else if (pid2 == 0) { //child2 (launch filter)
    
	        dup2(pipefd[1], 1);
    	        //close(pipefd[0]);
    	        close(pipefd[0]);
		char buffer[8];
		sprintf(buffer,"%d",pid);
                execl("procmon", "procmon", buffer, NULL);
                exit(0);
 	    }
   	    else { //parent2 

    	        pid_t pid3 = fork();
                if (pid3 == 0) { //child3 (launch procmon)
         
	            //printf("procmon running");
	            dup2(pipefd[0], 0); //send stdout to the pipe
	            close(pipefd[1]); //this descriptor is no longer needed
	            //close(pipefd[1]); //close reading end in the child
                    execl("filter", "filter", NULL);
                    exit(0);

                } else { //parent3
                    printf("PID of calcloop is %d\n", pid);
  		    printf("PID of procmon is %d\n", pid2);
		    sleep(20);
		    kill(pid,SIGTERM);
		    sleep(2);
		    kill(pid2,SIGTERM);
		    kill(pid3,SIGTERM);
		    return 0;
	        }
            }
	}
    }
}
