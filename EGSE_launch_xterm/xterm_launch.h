/* 
 * File:   xterm_launch.h
 * Author: byrdie
 *
 * Created on July 23, 2014, 9:53 AM
 */

#ifndef XTERM_LAUNCH_H
#define	XTERM_LAUNCH_H

#define _XOPEN_SOURCE
#define _GNU_SOURCE

#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <features.h>
#include <pthread.h>
#include <errno.h>

pthread_attr_t attrs;
pthread_t threads[2];

int masterfd, slavefd;

void * xterm_reader(void *);
void * egse_reader(void *);
int input_timeout(int, unsigned int);

#endif	/* XTERM_LAUNCH_H */

