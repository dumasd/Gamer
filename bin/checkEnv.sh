#!/usr/bin/env bash
BINDIR="${BINDIR:-/usr/bin}"
GAMER_PREFIX="${BINDIR}/.."

#check to see if the conf dir is given as an optional argument
if [ $# -gt 1 ]
then
    if [ "--config" = "$1" ]
	  then
	      shift
	      confdir=$1
	      shift
	      CONFDIR=$confdir
    fi
fi

if [ "x$CONFDIR" = "x" ]
then
  if [ -e "${GAMER_PREFIX}/conf" ]; then
    CONFDIR="$BINDIR/../conf"
  else
    CONFDIR="$BINDIR/../etc/gamer"
  fi
fi

if [ "x$CONF" = "x" ]
then
    CONF="conf.yaml"
fi

CONF="$CONFDIR/$CONF"

if [ -f "$CONFDIR/java.env" ]
then
    . "$CONFDIR/java.env"
fi

if [ "x${GAMER_LOG_DIR}" = "x" ]
then
    GAMER_LOG_DIR="$GAMER_PREFIX/logs"
fi

if [ "x${GAMER_LOG4J_PROP}" = "x" ]
then
    GAMER_LOG4J_PROP="INFO,CONSOLE"
fi

if [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    JAVA="$JAVA_HOME/bin/java"
elif type -p java; then
    JAVA=java
else
    echo "Error: JAVA_HOME is not set and java could not be found in PATH." 1>&2
    exit 1
fi

#add the CONF dir to classpath
CLASSPATH="$CONFDIR:$CLASSPATH"

for i in "$BINDIR"/../gamer-server/src/main/resources/lib/*.jar
do
    CLASSPATH="$i:$CLASSPATH"
done

#make it work in the binary package
#(use array for LIBPATH to account for spaces within wildcard expansion)
if ls "${GAMER_PREFIX}"/share/gamer/gamer-*.jar > /dev/null 2>&1; then
  LIBPATH=("${GAMER_PREFIX}"/share/gamer/*.jar)
else
  #release tarball format
  for i in "$BINDIR"/../gamer-*.jar
  do
    CLASSPATH="$i:$CLASSPATH"
  done
  LIBPATH=("${BINDIR}"/../lib/*.jar)
fi

for i in "${LIBPATH[@]}"
do
    CLASSPATH="$i:$CLASSPATH"
done

#make it work for developers
for d in "$BINDIR"/../build/lib/*.jar
do
   CLASSPATH="$d:$CLASSPATH"
done

for d in "$BINDIR"/../gamer-server/target/lib/*.jar
do
   CLASSPATH="$d:$CLASSPATH"
done

#make it work for developers
CLASSPATH="$BINDIR/../build/classes:$CLASSPATH"

#make it work for developers
CLASSPATH="$BINDIR/../gamer-server/target/classes:$CLASSPATH"

case "`uname`" in
    CYGWIN*|MINGW*) cygwin=true ;;
    *) cygwin=false ;;
esac

if $cygwin
then
    CLASSPATH=`cygpath -wp "$CLASSPATH"`
fi

echo "CLASSPATH=$CLASSPATH"

# default heap for server
ZK_SERVER_HEAP="${ZK_SERVER_HEAP:-1000}"
export SERVER_JVMFLAGS="-Xmx${ZK_SERVER_HEAP}m $SERVER_JVMFLAGS"
