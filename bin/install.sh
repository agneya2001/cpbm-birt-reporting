#!/bin/sh
 
#set -x
 
usage() { echo "Usage: $0 [ -a <ip of cpbm mysql host> -p <mysql port> -u <cpbm mysql username> -s <cpbm mysql password> -i <cpbm host ip>"; exit 1; }
 
a="unknown"
u="unknown"
p="unknown"
i="unknown"
s="unknown"
while getopts ":a:u:p:i:s:" o; do
    case "${o}" in
        a)
            a=${OPTARG}
            ;;
        p)
            p=${OPTARG}
            ;;
        u)
            u=${OPTARG}
            ;;
        s)
            s=${OPTARG}
            ;;
        i)
            i=${OPTARG}
            ;;
        *)
            usage
            ;;
    esac
done

if [ "$a" == "unknown" ]; then
    usage
    exit -1
fi
if [ "$u" == "unknown" ]; then
    usage
    exit -1
fi
if [ "$p" == "unknown" ]; then
    usage
    exit -1
fi
if [ "$i" == "unknown" ]; then
    usage
    exit -1
fi
if [ "$s" == "unknown" ]; then
    usage
    exit -1
fi

python=`python -c 'import sys; print(sys.version_info[0])'`

if [ $python -lt 2 ]; then
    echo "You should have python 2.7+ installed"
    exit -1
fi

ansible=`ansible --version | cut -d" " -f2`

if [[ $ansible != 1* ]]; then
    echo "Make sure ansible is installed"
    exit -1
fi

echo "[cpbm]" > cpbm
echo "$i" >> cpbm
ansible-playbook -u root -i cpbm setup_ui.yml
echo "Installed ui elements"

cp build_birt.yml run_build_birt.yml

sed -i '' "s/CPBM_MYSQL_IP/$a/" run_build_birt.yml
sed -i '' "s/CPBM_MYSQL_PORT/$p/" run_build_birt.yml
sed -i '' "s/CPBM_MYSQL_USER/$u/" run_build_birt.yml
sed -i '' "s/CPBM_MYSQL_PASSWORD/$s/" run_build_birt.yml

unset ANSIBLE_HOSTS
echo "localhost" > localhost
ansible-playbook -i localhost, run_build_birt.yml

echo "[cpbm]" > cpbm
echo "$i" >> cpbm
ansible-playbook -u root -i cpbm setup_birt.yml

echo "Installed reports done"
