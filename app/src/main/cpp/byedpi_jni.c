// JNI bridge that embeds byedpi (https://github.com/hufrea/byedpi) as an
// in-process SOCKS proxy. byedpi normally runs as a CLI (`ciadpi`); we reuse its
// argument parser and event loop directly.
//
// Lifecycle:
//   nativeStart(args)  -> parse_args + init + run  (blocks in the event loop)
//   nativeStop()       -> shutdown(server_fd) breaks the loop, run() returns
//
// byedpi keeps all configuration in the global `params`. To support starting the
// proxy more than once in a single process (VPN reconnect), we snapshot the
// pristine `params` on first use and restore it before every start.

#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include <getopt.h>
#include <sys/socket.h>

#include "byedpi/params.h"
#include "byedpi/proxy.h"

// Provided by byedpi (main.c / proxy.c).
extern int parse_args(int argc, char **argv);
extern int init(void);
extern void clear_params(char *line, char **argv);
extern void dump_all_cache(void);
extern int server_fd;

static struct params params_backup;
static int backup_saved = 0;

JNIEXPORT jint JNICALL
Java_io_github_yulbax_frkn_engine_ByeDpi_nativeStart(
        JNIEnv *env, jclass clazz, jobjectArray jargs) {

    // Snapshot the untouched defaults once; restore them on every later start so
    // a previous run's parsed state (groups, mempool pointers) does not leak.
    if (!backup_saved) {
        memcpy(&params_backup, &params, sizeof(params));
        backup_saved = 1;
    } else {
        // clear_params() frees the need_free *elements* but not the array buffer
        // itself; the restore below overwrites the pointer, so free it first to
        // avoid orphaning one need_free array per restart. free(NULL) is safe if
        // the previous run registered no cleanups.
        free(params.need_free);
        memcpy(&params, &params_backup, sizeof(params));
    }

    // Reset getopt so parse_args restarts cleanly on a subsequent invocation.
    optind = 1;
    optreset = 1;

    jsize argc = (*env)->GetArrayLength(env, jargs);
    char **argv = calloc((size_t) argc + 1, sizeof(char *));
    if (!argv) {
        return -1;
    }
    for (jsize i = 0; i < argc; i++) {
        jstring s = (jstring) (*env)->GetObjectArrayElement(env, jargs, i);
        const char *c = (*env)->GetStringUTFChars(env, s, NULL);
        argv[i] = strdup(c);
        (*env)->ReleaseStringUTFChars(env, s, c);
        (*env)->DeleteLocalRef(env, s);
    }

    jint result = 0;
    int status = parse_args((int) argc, argv);
    if (status) {
        result = status - 1;
    } else if (init() < 0) {
        result = -1;
    } else {
        // Blocks until nativeStop() shuts down server_fd.
        result = run(&params.laddr);
        dump_all_cache();
    }

    clear_params(NULL, NULL);
    for (jsize i = 0; i < argc; i++) {
        free(argv[i]);
    }
    free(argv);
    return result;
}

JNIEXPORT void JNICALL
Java_io_github_yulbax_frkn_engine_ByeDpi_nativeStop(
        JNIEnv *env, jclass clazz) {
    if (server_fd > 0) {
        shutdown(server_fd, SHUT_RDWR);
    }
}
