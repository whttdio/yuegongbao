#!/bin/sh
# Usage: ./ygb.sh {start|stop|restart|status}

AppName=yuegongbao-admin.jar
JVM_OPTS="-Dname=$AppName -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:NewRatio=1 -XX:SurvivorRatio=30 -XX:+UseParallelGC -XX:+UseParallelOldGC"

if [ "$1" = "" ]; then
    echo "Missing action: {start|stop|restart|status}"
    exit 1
fi

if [ "$AppName" = "" ]; then
    echo "Missing application name"
    exit 1
fi

start() {
    PID=`ps -ef | grep java | grep $AppName | grep -v grep | awk '{print $2}'`
    if [ x"$PID" != x"" ]; then
        echo "$AppName is running..."
    else
        nohup java $JVM_OPTS -jar $AppName > /dev/null 2>&1 &
        echo "Start $AppName success..."
    fi
}

stop() {
    echo "Stop $AppName"
    PID=""
    query() {
        PID=`ps -ef | grep java | grep $AppName | grep -v grep | awk '{print $2}'`
    }

    query
    if [ x"$PID" != x"" ]; then
        kill -TERM $PID
        echo "$AppName (pid:$PID) exiting..."
        while [ x"$PID" != x"" ]
        do
            sleep 1
            query
        done
        echo "$AppName exited."
    else
        echo "$AppName already stopped."
    fi
}

restart() {
    stop
    sleep 2
    start
}

status() {
    PID=`ps -ef | grep java | grep $AppName | grep -v grep | wc -l`
    if [ $PID != 0 ]; then
        echo "$AppName is running..."
    else
        echo "$AppName is not running..."
    fi
}

case $1 in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    status)
        status
        ;;
    *)
        echo "Unsupported action: $1"
        exit 1
        ;;
esac
