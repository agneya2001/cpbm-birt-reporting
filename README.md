cpbm-birt-reporting
===================

cpbm(2.3.0) birt integration to provide easily customisable and extendible reporting system. 

Installing BIRT runtime 
-----------------------

Download birt-runtime from http://download.eclipse.org/birt/downloads/build.php?build=R-R1-4_4_1-201409161320
birt-runtime-osgi-4_4_1-20140916.zip

Extract birt.war 

The birt.war is misisng the common loggings and mysql-jdbc driver.
Download the two and  injected in the war file.

Inject using below command:
jar -uvf birt.war WEB-INF/lib/commons-logging-1.2.jar

Copy birt.war to root@<cpbm-ip>:/usr/share/vts3/pickup/

If birt is installed correctly you will see http://<cpbm-ip-port>/birt displayig birt starting page.
Click on the sample report to ensure everything is correctly installed.

Now copy your reports in folder /usr/share/vts3/work/org.eclipse.virgo.kernel.deployer_3.0.3.RELEASE/staging/global/bundle/birt/0.0.0/birt.war

After above steps the BIRT report viewer will be running side by side with cpbm.

Adding reporting capabilities to CPBM UI
----------------------------------------

cd to cpbm-customization/citrix.cpbm.custom.all

do a maven build: mvn clean install -Dmaven.test.skip=true

Since changes are made to citrix.cpbm.custom.portal, the citrix.cpbm.custom.portal jar file needs to replace the equivalent jar file on cpbm installation.


Steps to deploy updated custom jar files:
		  

1. Stop the CloudPortal Business Manager server using the command:
    $ service cloud-portal stop 
					  
				
2. Go to /usr/share/vts3/config.
				
3. Open org.eclipse.virgo.repository.properties.
				
4.  Comment the following lines:
				  
	 default_impls.type=watched
	default_impls.watchDirectory=default_impls

				
5.  Uncomment the following lines:
				  
        custom_impls.type=watched
        custom_impls.watchDirectory=custom_impls

6. Modify the line to have custom_impls instead of default_impls
				  
    chain=ext,patches,usr,connectors,connectors_ext,default_impls,prop
				  with 
   chain=ext,custom_impls,patches,usr,connectors,connectors_ext,prop

				  Note:  Do not modify the jars under the default_impls directory.
				  
				
7. Copy the updated custom jars from target directory of all four custom projects to: 
	/usr/share/vts3/custom_impls
				  

				
8. Start the CloudPortal Business Manager server using the command:
	$ service cloud-portal start


You should see sample reports under Reports->IAAS Reports if you are logged in as root 
OR under Support->IAAS Reports if you are logged in as any other user.

Installing additional reports
-----------------------------

Use BIRT designer to plan and design a report that uses cpbm DB as the data source.
cp the report in the birt/reports directory and inject it in birt.war
Modify the birt/index.jsp to place it in the right section and replce this in birt.war.
replace the birt.war on cpbm as shown above.
