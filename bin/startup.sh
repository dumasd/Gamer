#!/usr/bin/env bash

# use POSIX interface, symlink is followed automatically
BIN="${BASH_SOURCE-$0}"
BIN="$(dirname "${BIN}")"
BINDIR="$(cd "${BIN}"; pwd)"

MAIN_CLASS="com.thinkerwolf.gamer.test.NettyServerTests"

if [ -e "$BIN/../libexec/checkEnv.sh" ]; then
  . "$BINDIR"/../libexec/checkEnv.sh
else
  . "$BINDIR"/checkEnv.sh
fi

# JMX设置
if [ "x$JMXLOCALONLY" = "x" ]
then
    JMXLOCALONLY=false
fi

if [ "x$JMXDISABLE" = "x" ] || [ "$JMXDISABLE" = 'false' ]
then
  echo "gamer JMX enabled by default" >&2
  if [ "x$JMXPORT" = "x" ]
  then
    # for some reason these two options are necessary on jdk6 on Ubuntu
    #   accord to the docs they are not necessary, but otw jconsole cannot
    #   do a local attach
    MAIN="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=$JMXLOCALONLY $MAIN_CLASS"
  else
    if [ "x$JMXAUTH" = "x" ]
    then
      JMXAUTH=false
    fi
    if [ "x$JMXSSL" = "x" ]
    then
      JMXSSL=false
    fi
    if [ "x$JMXLOG4J" = "x" ]
    then
      JMXLOG4J=true
    fi
    echo "gamer remote JMX Port set to $JMXPORT" >&2
    echo "gamer remote JMX authenticate set to $JMXAUTH" >&2
    echo "gamer remote JMX ssl set to $JMXSSL" >&2
    echo "gamer remote JMX log4j set to $JMXLOG4J" >&2
    MAIN="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=$JMXPORT -Dcom.sun.management.jmxremote.authenticate=$JMXAUTH -Dcom.sun.management.jmxremote.ssl=$JMXSSL -Dgamer.jmx.log4j.disable=$JMXLOG4J org.apache.gamer.server.quorum.QuorumPeerMain"
  fi
else
    echo "JMX disabled by user request" >&2
    MAIN="org.apache.gamer.server.quorum.QuorumPeerMain"
fi

if [ "x$SERVER_JVMFLAGS" != "x" ]
then
    JVMFLAGS="$SERVER_JVMFLAGS $JVMFLAGS"
fi

if [ "x$2" != "x" ]
then
    CONF="$CONFDIR/$2"
fi

# if we give a more complicated path to the config, don't screw around in $CONFDIR
if [ "x$(dirname "$CONF")" != "x$CONFDIR" ]
then
    CONF="$2"
fi

if $cygwin
then
    CONF=`cygpath -wp "$CONF"`
    # cygwin has a "kill" in the shell itself, gets confused
    KILL=/bin/kill
else
    KILL=kill
fi

echo "Using config: $CONF" >&2

case "$OSTYPE" in
*solaris*)
  GREP=/usr/xpg4/bin/grep
  ;;
*)
  GREP=grep
  ;;
esac

ZOO_DATADIR="$($GREP "^[[:space:]]*dataDir" "$CONF" | sed -e 's/.*=//')"
ZOO_DATADIR="$(echo -e "${ZOO_DATADIR}" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"

if [ -z "$GAMERPIDFILE" ]; then
    GAMERPIDFILE="$ZOO_DATADIR/gamer_server.pid"
else
    # ensure it exists, otw stop will fail
    mkdir -p "$(dirname "$GAMERPIDFILE")"
fi

if [ ! -w "$GAMER_LOG_DIR" ] ; then
mkdir -p "$GAMER_LOG_DIR"
fi

GAMER_LOG_FILE=gamer-$USER-server-$HOSTNAME.log
_ZOO_DAEMON_OUT="$GAMER_LOG_DIR/gamer-$USER-server-$HOSTNAME.out"

GAMER_LOG_CONFIG_FILE="${CONFDIR}\logback.xml"

LOG_PROPS="-Dgamer.log.dir=${GAMER_LOG_DIR} \
           -Dgamer.log.file=${GAMER_LOG_FILE} \
           -Dgamer.root.logger=${GAMER_LOG4J_PROP} \
           -Dgamer.log.configFile=${GAMER_LOG_CONFIG_FILE}"

echo "MAIN=$MAIN"

case $1 in
start)
    echo  -n "Starting gamer ... "
    if [ -f "$GAMERPIDFILE" ]; then
      if kill -0 `cat "$GAMERPIDFILE"` > /dev/null 2>&1; then
         echo $command already running as process `cat "$GAMERPIDFILE"`.
         exit 1
      fi
    fi
    nohup "$JAVA" "${LOG_PROPS}" \
    -XX:+HeapDumpOnOutOfMemoryError -XX:OnOutOfMemoryError='kill -9 %p' \
    -cp "$CLASSPATH" $JVMFLAGS $MAIN "$CONF" > "$_ZOO_DAEMON_OUT" 2>&1 < /dev/null &
    if [ $? -eq 0 ]
    then
      case "$OSTYPE" in
      *solaris*)
        /bin/echo "${!}\\c" > "$GAMERPIDFILE"
        ;;
      *)
        /bin/echo -n $! > "$GAMERPIDFILE"
        ;;
      esac
      if [ $? -eq 0 ];
      then
        sleep 1
        pid=$(cat "${GAMERPIDFILE}")
        if ps -p "${pid}" > /dev/null 2>&1; then
          echo STARTED
        else
          echo FAILED TO START
          exit 1
        fi
      else
        echo FAILED TO WRITE PID
        exit 1
      fi
    else
      echo SERVER DID NOT START
      exit 1
    fi
    ;;
start-foreground)
    ZOO_CMD=(exec "$JAVA")
    if [ "${ZOO_NOEXEC}" != "" ]; then
      ZOO_CMD=("$JAVA")
    fi
    "${ZOO_CMD[@]}" "-Dgamer.log.dir=${GAMER_LOG_DIR}" \
    "-Dgamer.log.file=${GAMER_LOG_FILE}" "-Dgamer.root.logger=${GAMER_LOG4J_PROP}" \
    -XX:+HeapDumpOnOutOfMemoryError -XX:OnOutOfMemoryError='kill -9 %p' \
    -cp "$CLASSPATH" $JVMFLAGS $MAIN "$CONF"
    ;;
print-cmd)
    echo "\"$JAVA\" -Dgamer.log.dir=\"${GAMER_LOG_DIR}\" \
    -Dgamer.log.file=\"${GAMER_LOG_FILE}\" -Dgamer.root.logger=\"${GAMER_LOG4J_PROP}\" \
    -XX:+HeapDumpOnOutOfMemoryError -XX:OnOutOfMemoryError='kill -9 %p' \
    -cp \"$CLASSPATH\" $JVMFLAGS $MAIN \"$CONF\" > \"$_ZOO_DAEMON_OUT\" 2>&1 < /dev/null"
    ;;
stop)
    echo -n "Stopping gamer ... "
    if [ ! -f "$GAMERPIDFILE" ]
    then
      echo "no gamer to stop (could not find file $GAMERPIDFILE)"
    else
      $KILL $(cat "$GAMERPIDFILE")
      rm "$GAMERPIDFILE"
      sleep 1
      echo STOPPED
    fi
    exit 0
    ;;
version)
    MAIN=org.apache.gamer.version.VersionInfoMain
    $JAVA -cp "$CLASSPATH" $MAIN 2> /dev/null
    ;;
restart)
    shift
    "$0" stop ${@}
    sleep 3
    "$0" start ${@}
    ;;
status)
    # -q is necessary on some versions of linux where nc returns too quickly, and no stat result is output
    clientPortAddress=`$GREP "^[[:space:]]*clientPortAddress[^[:alpha:]]" "$CONF" | sed -e 's/.*=//'`
    if ! [ $clientPortAddress ]
    then
	clientPortAddress="localhost"
    fi
    clientPort=`$GREP "^[[:space:]]*clientPort[^[:alpha:]]" "$CONF" | sed -e 's/.*=//'`
    if ! [[ "$clientPort"  =~ ^[0-9]+$ ]]
    then
       dataDir=`$GREP "^[[:space:]]*dataDir" "$CONF" | sed -e 's/.*=//'`
       myid=`cat "$dataDir/myid"`
       if ! [[ "$myid" =~ ^[0-9]+$ ]] ; then
         echo "clientPort not found and myid could not be determined. Terminating."
         exit 1
       fi
       clientPortAndAddress=`$GREP "^[[:space:]]*server.$myid=.*;.*" "$CONF" | sed -e 's/.*=//' | sed -e 's/.*;//'`
       if [ ! "$clientPortAndAddress" ] ; then
           echo "Client port not found in static config file. Looking in dynamic config file."
           dynamicConfigFile=`$GREP "^[[:space:]]*dynamicConfigFile" "$CONF" | sed -e 's/.*=//'`
           clientPortAndAddress=`$GREP "^[[:space:]]*server.$myid=.*;.*" "$dynamicConfigFile" | sed -e 's/.*=//' | sed -e 's/.*;//'`
       fi
       if [ ! "$clientPortAndAddress" ] ; then
          echo "Client port not found. Terminating."
          exit 1
       fi
       if [[ "$clientPortAndAddress" =~ ^.*:[0-9]+ ]] ; then
          clientPortAddress=`echo "$clientPortAndAddress" | sed -e 's/:.*//'`
       fi
       clientPort=`echo "$clientPortAndAddress" | sed -e 's/.*://'`
       if [ ! "$clientPort" ] ; then
          echo "Client port not found. Terminating."
          exit 1
       fi
    fi
    echo "Client port found: $clientPort. Client address: $clientPortAddress."
    STAT=`"$JAVA" "-Dgamer.log.dir=${GAMER_LOG_DIR}" "-Dgamer.root.logger=${GAMER_LOG4J_PROP}" "-Dgamer.log.file=${GAMER_LOG_FILE}" \
             -cp "$CLASSPATH" $JVMFLAGS org.apache.gamer.client.FourLetterWordMain \
             $clientPortAddress $clientPort srvr 2> /dev/null    \
          | $GREP Mode`
    if [ "x$STAT" = "x" ]
    then
        echo "Error contacting service. It is probably not running."
        exit 1
    else
        echo $STAT
        exit 0
    fi
    ;;
*)
    echo "Usage: $0 [--config <conf-dir>] {start|start-foreground|stop|version|restart|status|print-cmd}" >&2

esac
