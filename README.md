cpbm-birt-reporting
===================

cpbm birt integration to provide easily customisable and extendible reporting system. 

Installing BIRT runtime 
-----------------------

Download birt-runtime from http://download.eclipse.org/birt/downloads/build.php?build=R-R1-4_4_1-201409161320
birt-runtime-osgi-4_4_1-20140916.zip

Extract birt.war 

The birt.war is misisng the common loggings and this needs ot be injected in the war file.

Download commons logging from link below:
http://mirrors.ibiblio.org/apache//commons/logging/binaries/commons-logging-1.2-bin.tar.gz
mv it to WEB-INF/lib directory where your birt.war is.
inject using below command:
jar -uvf birt.war WEB-INF/lib/commons-logging-1.2.jar

Copy birt.war to /usr/share/vts3/pickup/

A fixed birt.war file is available in the repo.

restart cloud-portal

If birt is installed correctly you will see http://<cpbm-ip-port>/birt displayig birt startign page.
Click on the sample report to ensure everything is correctly installed.

