/* 
 * File:   main.c
 * Author: Roy Smart
 *
 * Created on July 21, 2014, 2:14 PM
 */

#include "xterm_launch.h"

/*
 * 
 */
int main(int argc, char** argv) {

    char xterm_args[255];
    char * slavedevice;

    

    masterfd = posix_openpt(O_RDWR | O_NOCTTY);


    if (masterfd == -1
            || grantpt(masterfd) == -1
            || unlockpt(masterfd) == -1
            || (slavedevice = ptsname(masterfd)) == NULL)
        return -1;

    if (slavefd < 0)
        return -1;

    slavefd = open(slavedevice, O_RDWR | O_NOCTTY);

    pid_t result = fork();

    if (result == 0) { //this is the child


        /*redirect stdout*/

        //        close(STDOUT_FILENO);
        //        dup(slavefd);
        close(slavefd);
        //        close(masterfd);

        slavedevice = slavedevice + 9;
        printf("slave device is: %s\n", slavedevice);

        sprintf(xterm_args, "-S%s/%d -ls", slavedevice, masterfd);
        puts(xterm_args);

        /*launch xterm*/
        //        system(xterm_args);
        execlp("xterm", "xterm", xterm_args, (char*) 0);

        printf("execlp returned\n");
        return (EXIT_FAILURE);

    }



//    sleep(1);

    close(masterfd);


    //    char buf[255] = {0};

    /*redirect stdin*/
    //    close(STDIN_FILENO);
    //    dup(masterfd);


    /*set thread attributes*/
    pthread_attr_init(&attrs);
    pthread_attr_setdetachstate(&attrs, PTHREAD_CREATE_JOINABLE);

    /*start listener threads*/
    pthread_create(&threads[0], &attrs, (void *(*)(void *))xterm_reader, NULL);
    pthread_create(&threads[1], &attrs, (void *(*)(void *))egse_reader, NULL);

    wait(0);

    return (EXIT_SUCCESS);
}

/*Thread 1*/
void * xterm_reader(void * arg) {
    char c;

        while (1) {
            if (input_timeout(slavefd, 1) > 0) {
                read(slavefd, &c, 1);
                write(STDOUT_FILENO, &c, 1);
            }
        }
}

/*Thread 2*/
void * egse_reader(void * arg) {
    char c;

        while (1) {
            if (input_timeout(STDIN_FILENO, 1) > 0) {
                read(STDIN_FILENO, &c, 1);
                write(slavefd, &c, 1);
            }
        }
}

int input_timeout(int filedes, unsigned int seconds) {
    fd_set set;
    struct timeval timeout;

    /*initialize the file descriptor set for select function*/
    FD_ZERO(&set);
    FD_SET(filedes, &set);

    /*initialize timout data structure for select function*/
    timeout.tv_sec = seconds;
    timeout.tv_usec = 0;

    /*select returns 0 if timeout, 1 if input data is available, -1 if error*/
    return TEMP_FAILURE_RETRY(select(FD_SETSIZE, &set, NULL, NULL, &timeout));
}

