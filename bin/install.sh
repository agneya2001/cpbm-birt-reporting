#!/bin/sh
 
set -x
 
usage() { echo "Usage: $0 [ -a <ip of cpbm mysql host> -p <mysql port> -u <cpbm mysql username> -s <cpbm mysql password> -i <cpbm host ip>"; exit 1; }

passw="password"
 
#check os version
os="unknown"
if type sw_vers >/dev/null 2>&1; then
    if sw_vers | grep --quiet "Mac OS X"; then 
        os="mac" 
    fi
fi
if [ -f /etc/redhat-release ]; then
    if grep --quiet "CentOS *6*" /etc/redhat-release; then
        os="centos"
    fi
fi

if [ "$os" == "unknown" ]; then
    echo "Only Mac Os and Centos 6.* supported now"
fi

#install ansible
if [ "$os" == "centos" ]; then
    if  ! type ansible >/dev/null 2>&1; then
        echo "Installing Ansible"
        #yum update -y
        rpm -ivh http://dl.fedoraproject.org/pub/epel/6/x86_64/epel-release-6-8.noarch.rpm
        yum install -y python-pip
        sudo yum install ansible -y
    fi
    sudo yum install libselinux-python sshpass -y
    #install java
    sudo yum install java-1.7.0-openjdk-devel -y
    export JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.85.x86_64
fi

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
echo "Enter CPBM root password->"
ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook -u root -i cpbm setup_ui.yml --ask-pass
echo "Installed ui elements"

cp build_birt.yml run_build_birt.yml

if [ "$os" == "centos" ]; then
    sed -i "s/CPBM_MYSQL_IP/$a/" run_build_birt.yml
    sed -i "s/CPBM_MYSQL_PORT/$p/" run_build_birt.yml
    sed -i "s/CPBM_MYSQL_USER/$u/" run_build_birt.yml
    sed -i "s/CPBM_MYSQL_PASSWORD/$s/" run_build_birt.yml
elif [  "$os" == "mac" ]; then
    sed -i '' "s/CPBM_MYSQL_IP/$a/" run_build_birt.yml
    sed -i '' "s/CPBM_MYSQL_PORT/$p/" run_build_birt.yml
    sed -i '' "s/CPBM_MYSQL_USER/$u/" run_build_birt.yml
    sed -i '' "s/CPBM_MYSQL_PASSWORD/$s/" run_build_birt.yml
else
    echo "Unknow os, only CentOs and Mac os supported now"
    exit
fi


unset ANSIBLE_HOSTS
echo "localhost" > localhost
ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook -i localhost, run_build_birt.yml 

echo "[cpbm]" > cpbm
echo "$i" >> cpbm
echo "Enter CPBM root password->"
ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook -u root -i cpbm setup_birt.yml --ask-pass

echo "Installed reports done"
