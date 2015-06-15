cpbm-birt-reporting
===================

cpbm(2.3.0) birt integration to provide easily customisable and extendible reporting system. 

This adds following capability to cpbm:

1. Easily extensible and functional reporting system.

2. User customizable reports.

3. Variety of output formats like pdf, doc and csv.

4. Printer friendly reports.

5. User customizable reports

Installing BIRT runtime 
-----------------------

Download birt-runtime from http://download.eclipse.org/birt/downloads/build.php?build=R-R1-4_4_1-201409161320
birt-runtime-osgi-4_4_1-20140916.zip

Extract birt.war 

The birt.war is missng the common loggings and mysql-jdbc driver.
Download the two and  injected in the war file.

The two jars are provided here for convenience. Copy birt.war in birt folder and then inject the two jars:
cd birt
jar -uvf birt.war WEB-INF/lib/*

inject the sample reports as:
jar -uvf birt.war reports/*

inject the index.jsp
jar -uvf birt.war index.jsp

Copy birt.war to root@<cpbm-ip>:/usr/share/vts3/pickup/

After above steps the BIRT report viewer will be running side by side with cpbm.

Adding reporting capabilities to CPBM UI
----------------------------------------

cd to cpbm-customization/citrix.cpbm.custom.all

The changes to display the IAAS Reports tab to display BIRT reports are already there in the repo.

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
				  
				
7. Copy the updated custom jars from target directory of all three custom projects to: 
	/usr/share/vts3/custom_impls

	These are the jar files that need to be copied: 
		citrix.cpbm.custom.common-2.3.0.jar  
		citrix.cpbm.custom.model-2.3.0.jar  
		citrix.cpbm.custom.portal-2.3.1.jar				  

	The respective folders are:

	./cpbm-customization/citrix.cpbm.custom.common/target/citrix.cpbm.custom.common-2.3.1.jar
	./cpbm-customization/citrix.cpbm.custom.model/target/citrix.cpbm.custom.model-2.3.1.jar	
	./cpbm-customization/citrix.cpbm.custom.portal/target/citrix.cpbm.custom.portal-2.3.1.jar

				
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
