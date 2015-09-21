#!/bin/bash

set -x

mkdir cpbm-bundle

#build 
cp ../pb/build_birt.yml cpbm-bundle/.
cp ../pb/setup_birt.yml cpbm-bundle/.

cp -r ../birt cpbm-bundle/.

cp install.sh cpbm-bundle/.

pwd=`pwd`

unset ANSIBLE_HOSTS
echo "localhost" > localhost
ansible-playbook -i localhost, ../pb/build_ui.yml

cd $pwd

cp ../cpbm-customization/citrix.cpbm.custom.common/target/citrix.cpbm.custom.common-2.3.1.jar cpbm-bundle/.
cp ../cpbm-customization/citrix.cpbm.custom.model/target/citrix.cpbm.custom.model-2.3.1.jar cpbm-bundle/.
cp ../cpbm-customization/citrix.cpbm.custom.portal/target/citrix.cpbm.custom.portal-2.3.1.jar cpbm-bundle/.

cp ../pb/setup_ui.yml cpbm-bundle/.

