#!/bin/sh
 
#set -x
 
usage() { echo "Usage: $0 [ -b <the base file> -t <the target file>"; exit 1; }
 
b="unknown"
t="unknown"
while getopts ":b:t:" o; do
    case "${o}" in
        b)
            b=${OPTARG}
            ;;
        t)
            t=${OPTARG}
            ;;
        *)
            usage
            ;;
    esac
done

if [ "$b" == "unknown" ]; then
    echo "You need to provice the name of base filei as parameter -i";
    exit -1
fi
if [ "$t" == "unknown" ]; then
    echo "You need to provice the name of the translated file as parameter -t";
    exit -1
fi

array=()

i=0
while read line # Read a line
do
    if [[ $line != \#* ]]; then
        name=`echo $line | cut -d '=' -f 1`
        if [ "$name" != "" ]; then
            array[i]=$name # Put it into the array
            i=$(($i + 1))
        fi
    fi
done < $b

marray=()

i=0
while read line # Read a line
do
    if [[ $line != \#* ]]; then
        name=`echo $line | cut -d '=' -f 1`
        if [ "$name" != "" ]; then
            marray[i]=$name # Put it into the array
            i=$(($i + 1))
        fi
    fi
done < $t


echo > result.txt
for i in "${array[@]}"
do
    matched="false"
    for j in "${marray[@]}"
    do
        if [ "$i" == "$j" ]; then
            echo ">> $j"
            echo ">> $j" >> result.txt
            matched="true"
        fi
    done
    if [ "$matched" == "false" ]; then
        echo "++$i"
        echo "++$i" >> result.txt
    fi
done




