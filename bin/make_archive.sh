#!/bin/bash

cpbm_mylivechatid=56622152

#set -x

#check os version
os="unknown"
if type sw_vers >/dev/null 2>&1; then
    if sw_vers | grep --quiet "Mac OS X"; then 
        os="mac" 
    fi
fi
if grep --quiet "CentOS *6*" /etc/redhat-release; then
    os="centos"
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
    sudo yum install libselinux-python wget -y
    #install java
    sudo yum install java-1.7.0-openjdk-devel -y
    export JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.85.x86_64
    #install mvn
    if  ! type mvn >/dev/null 2>&1; then
        wget http://mirror.cc.columbia.edu/pub/software/apache/maven/maven-3/3.3.3/binaries/apache-maven-3.3.3-bin.tar.gz
        sudo tar xzf apache-maven-3.3.3-bin.tar.gz -C /usr/local
    fi
    export M2_HOME=/usr/local/apache-maven-3.3.3
    export PATH=${M2_HOME}/bin:${PATH}
    echo `mvn -version`
fi

[ -d cpbm-bundle ] && rm -rf cpbm-bundle 
mkdir cpbm-bundle

#build 
cp ../pb/build_birt.yml cpbm-bundle/.
cp ../pb/setup_birt.yml cpbm-bundle/.

cp -r ../birt cpbm-bundle/.

cp install.sh cpbm-bundle/.

pwd=`pwd`

sed -i 's/#HCCID#/$cpbm_mylivechatid/g' ../cpbm-customization/citrix.cpbm.custom.portal/src/main/resources/WEB-INF/jsp/tiles/shared/footer.jsp

cd ./../cpbm-customization/citrix.cpbm.custom.all
 
mvn clean install -Dmaven.test.skip=true 

cd $pwd

cp ../cpbm-customization/citrix.cpbm.custom.common/target/citrix.cpbm.custom.common-2.3.1.jar cpbm-bundle/.
cp ../cpbm-customization/citrix.cpbm.custom.model/target/citrix.cpbm.custom.model-2.3.1.jar cpbm-bundle/.
cp ../cpbm-customization/citrix.cpbm.custom.portal/target/citrix.cpbm.custom.portal-2.3.1.jar cpbm-bundle/.

cp ../pb/setup_ui.yml cpbm-bundle/.
cp ../docs/cpbm-bundle.txt cpbm-bundle/README.txt

echo ".."
echo "CPBM customised bundle [cpbm-bundle] build successful !"
echo ".."
