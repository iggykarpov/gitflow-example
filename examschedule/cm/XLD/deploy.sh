#!/bin/bash
#---------------------------------------------------------------------------------------------------------------------#
# AUTHOR        : Jay.Renbaum@finra.org                                                                               #
# VERSION       : 1.0                                                                                                 #
# DESCRIPTION   : This script is used to automate the tomcat component deployment via the appid and tcadmin accounts. #
#                 The script reads the component specific deploy.properties file and the buildinfo from SOURCEDIR and #
#                 executes commands as requested by BladeLogic.                                                       #
#---------------------------------------------------------------------------------------------------------------------#
DATE=`date +%m%d%Y%H%M%S`
appsudo=blep
tcsudo=springs
tcadmin="/usr/local/sse/tools/2.0/tcadmin"
notify="Avinash.Chukka@finra.org, Saurabh.Varma2finra.org"

#-----------------------------------------------------------------------------------------------------------------#
#                                       MAIN SECTION OF SCRIPT                                                    #
#-----------------------------------------------------------------------------------------------------------------#

# process paramaters passed in by BladeLogic
if [ $# -ne 2 ]
then
    echo -e "\nUsage: $0 targetenv action"
    echo -e "\nExamples:\n$0 dev undeploy\n0 qc stage\n$0 prod start" 
    exit 1
else
    targetenv=$1 
    action=$2

    # Check targetenv
    if [ "$targetenv" = "dev"   -o \
         "$targetenv" = "lab"   -o \
         "$targetenv" = "int"   -o \
         "$targetenv" = "qc"    -o \
         "$targetenv" = "qc2"   -o \
         "$targetenv" = "qcpm"  -o \
         "$targetenv" = "qcpm2" -o \
         "$targetenv" = "train" -o \
         "$targetenv" = "prod"  -o \
         "$targetenv" = "prod2" ]
    then 
        echo -e "\ntargetenv     : $targetenv" 
        TARGETENV_UPPER=`echo ${targetenv} | tr "[:lower:]" "[:upper:]"` 
    else 
        echo -e "\nUnknown targetenv: $targetenv. Use dev/lab/int/qc/qc2/train/prod/prod2"
        exit 1
    fi
fi


#-----------------------------------------------------------------------------------------------------------------#
#                                    ENSURING OS SPECIFIC DIFFERENCES                                             #
#-----------------------------------------------------------------------------------------------------------------#
if [ `uname` = "Linux" ] ; then
    export JDKVERSION=jdk160
    export JAVA_HOME=/usr/local/sse/jdk/${JDKVERSION}
    export PATH=/bin:/usr/bin:/usr/sbin:/usr/local/bin:${JAVA_HOME}/bin:$PATH
else
    export PATH=/bin:/usr/bin:/usr/sbin:/usr/local/bin:$PATH
fi

#-----------------------------------------------------------------------------------------------------------------#
#                                    READ BUILDINFO FILE TO OBTAIN BUILD PARAMS                                   #
#-----------------------------------------------------------------------------------------------------------------#
if [ -f "buildinfo" ]
then
    # Determine the Component value for this build:
    export component=`grep "^@(#).*Component:" buildinfo | awk ' { print $NF } '`
    if [ -z "$component" ]
    then 
        echo "Component not found in buildinfo file. Exiting script." 
        exit 1
    else 
        if [ "$component" = "EP_PLAN" ]
        then
            export c_abbr="ep"  
        elif [ "$component" = "EP_CLOSE" ]
        then
            export c_abbr="ec"  
        elif [ "$component" = "EP_MM" ]
        then
            export c_abbr="mm" 
        elif [ "$component" = "EP_STAFF" ]
        then
            export c_abbr="es" 
        else
            echo -e "\nComponent value found in buildinfo file was not expected: \"${component}\". Exiting script." 
            exit 1
        fi
    fi

    # Initialize the logfile and emailfile variables now that we know the component value
    logfile="/tmp/$c_abbr.$1.$2.$DATE.$$.txt"
    emailfile="/tmp/$c_abbr.$1.$2.$DATE.$$"

    # Delete any versions of the logfile or emailfile that may exist from a previous deploy attempt for the same build
    if [ -f "$emailfile" ] ; then rm $emailfile ; fi
    if [ -f "$logfile" ] ; then rm $logfile ; fi

    # Delete old emailfiles and logfiles for this particular targetenv and action
    find /tmp -name "$c_abbr.$2.$3*" -type f -mtime +1 -exec rm -f '{}' \; >/dev/null 2>&1
    find /tmp -name "$c_abbr.$1.$2*" -type f -mtime +1 -exec rm -f '{}' \; >/dev/null 2>&1

    echo -e "----------------------------------"   | tee -a $logfile
    echo -e "\nStarting Action: $action"            | tee -a $logfile
    echo -e "\n----------------------------------" | tee -a $logfile
    echo -e "\nDate           : `date`"            | tee -a $logfile
    echo -e "Component      : $component"        | tee -a $logfile 
    echo -e "Component abbr : $c_abbr"           | tee -a $logfile 

    echo -e "PATH           : $PATH"           | tee -a $logfile 
    #-----------------------------------------------------------------------------------------------------------------#
    #                                VALIDATE ADDITIONAL REQUIRED DEPLOYMENT PARAMETERS                               #
    #-----------------------------------------------------------------------------------------------------------------#

    export project=`grep "Project:" buildinfo | awk ' { print $NF } '`                         
    if [ -z "$project" ]  ; then echo "project not set, exiting ..."  ; exit 1 ; else echo -e "Project        : $project"  | tee -a $logfile ; fi

    export application=`grep "Application:" buildinfo | awk ' { print $NF } '`                         
    if [ -z "$application" ]  ; then echo "application not set, exiting ..."  ; exit 1 ; else echo -e "Application    : $application"  | tee -a $logfile ; fi
    export buildid=`grep "CM Label:" buildinfo | awk ' { print $NF } '`
    if [ -z "$buildid" ]  ; then echo "buildid not set" ; else echo -e "Buildid        : $buildid" | tee -a $logfile ; fi
    export svn_url=`grep "SVN URL:" buildinfo | awk ' { print $NF } '`
    if [ -z "$svn_url" ]  ; then echo "svn_url not set" ; else echo -e "SVN URL        : $svn_url" | tee -a $logfile ; fi

else 
    echo -e "\nCould not read the buildinfo file, exiting..."
    exit 1
fi


#-----------------------------------------------------------------------------------------------------------------#
#                                 FIND THE APPLICATION's DEPLOY.PROPERTIES FILE                                   #
#-----------------------------------------------------------------------------------------------------------------#
echo -e "\n----------------------------------"        | tee -a $logfile
echo -e "\nLooking for the deploy.properties file..." | tee -a $logfile

if [ -f "deploy.properties" ] 
then
    export propertyfile="deploy.properties"
    echo -e "\nFound property file: $propertyfile" | tee -a $logfile
    echo -e "\n----------------------------------" | tee -a $logfile
else
    echo "Property file deploy.properties not found" 
    exit 1
fi

#-----------------------------------------------------------------------------------------------------------------#
#                          OBTAIN REQUIRED DEPLOYMENT PARAMETERS FROM THE DEPLPOY.PROPERTIES FILE                 #
#-----------------------------------------------------------------------------------------------------------------#

# Obtain INSTANCE:
eval `grep "${TARGETENV_UPPER}:" $propertyfile | eval sed 's/${TARGETENV_UPPER}://g'`
if [ -z "$INSTANCE" ] ; then echo "instance not set, exiting ..." ; exit 1 ; else echo -e "\ninstance    : $INSTANCE" | tee -a $logfile ; fi

# Obtain name of war file and environment specific properties.zip file:
eval `grep "GLOBAL:" $propertyfile | eval sed 's/GLOBAL://g'`
prop_zip_file="${FILE_NAME}-${targetenv}.zip"
war_file="${FILE_NAME}.war"

echo -e "zip file    : $prop_zip_file" | tee -a $logfile
echo -e "war file    : $war_file"      | tee -a $logfile
echo -e "host        : `hostname`"     | tee -a $logfile
echo -e "osinfo      : `uname`"        | tee -a $logfile
echo -e "targetenv   : $targetenv"     | tee -a $logfile
echo -e "logfile     : $logfile"       | tee -a $logfile

echo -e "\n----------------------------------" | tee -a $logfile

#-----------------------------------------------------------------------------------------------------------------#
#                          SENDMAIL SUBROUTINES FOR SENDING SUCCESS AND FAILURE EMAILS                            #
#-----------------------------------------------------------------------------------------------------------------#

function passemail {

    SUBJECT="SUCCESS : $project : $component $targetenv $action message for buildid: $buildid"
    echo -e "From: $LOGNAME\nTo: $notify\nSubject: $SUBJECT\nMime-Version: 1.0\nContent-Type: text/html\n" > $emailfile
    #echo -e "From: Jay.Renbaum@finra.org\nTo: $notify\nSubject: $SUBJECT\nMime-Version: 1.0\nContent-Type: text/html\n" > $emailfile
    echo "<html>" >> $emailfile
    echo "<body>" >> $emailfile
    echo "<pre>" >> $emailfile
    awk ' { print $0 } ' $logfile | sed -e 's/Success running .*/<div align=\"center\" style=\"background: green ; font-size: 24px\">SUCCESS<\/div>/g' >> $emailfile
    echo "</pre>"  >> $emailfile
    echo "</body>" >> $emailfile
    echo "</html>" >> $emailfile
    sendmail -t -oi < $emailfile
    #cat $logfile | mail -s "$SUBJECT" $notify
}

function failemail {
  
    SUBJECT="FAILURE : $project : $component $targetenv $action message for buildid: $buildid"
    echo -e "From: $LOGNAME\nTo: $notify\nSubject: $SUBJECT\nMime-Version: 1.0\nContent-Type: text/html\n" > $emailfile
    #echo -e "From: Jay.Renbaum@rfinra.org\nTo: $notify\nSubject: $SUBJECT\nMime-Version: 1.0\nContent-Type: text/html\n" > $emailfile
    echo "<html>" >> $emailfile
    echo "<body>" >> $emailfile
    echo "<pre>" >> $emailfile
    awk ' { print $0 } ' $logfile | sed -e 's/Failure running.*/<div align=\"center\" style=\"background: red ; font-size: 24px\">FAILURE<\/div>/g' >> $emailfile
    echo "</pre>"  >> $emailfile
    echo "</body>" >> $emailfile
    echo "</html>" >> $emailfile
    sendmail -t -oi < $emailfile
    #cat $logfile | mail -s "$SUBJECT" $notify

    exit 1
}

#-----------------------------------------------------------------------------------------------------------------#
#                                         START CREATION OF DEPLOYMENT ACTIONS                                    #
#                                        This is where the rubber hits the road                                   #
#-----------------------------------------------------------------------------------------------------------------#
case "$action" in 

#-----------------------------------------------------------------------------------------------------------------#
#                                   STAGE JOB TO COPY WAR/EAR ON THE APP SERVER                                   #
#-----------------------------------------------------------------------------------------------------------------#
stage)
    if [ ! -f "$war_file" ] || [ ! -f $prop_zip_file ]
    then
        echo -e "\nThe war file ($war_file) and or properties zip file ($prop_zip_file) was not found as expected. Exiting script." | tee -a $logfile
        failemail
    fi #

    echo -e "\nFound war file ($war_file) and properties zip file ($prop_zip_file). Ready to deploy..." | tee -a $logfile

    #-----------------------------------------------------------------------------------------------------------------#
    #                                          COPY WAR/EAR TO TARGET LOCATION                                        #
    #-----------------------------------------------------------------------------------------------------------------#

    # Remove old version of files from webapps folder
    echo -e "\nRemoving old version of the war file and properties zip file from the webapps folder..." | tee -a $logfile
    sudo -u $appsudo sh -c "rm -f /app/proj/$INSTANCE/webapps/$war_file" >> $logfile 2>&1
    sudo -u $appsudo sh -c "rm -f /app/proj/$INSTANCE/webapps/$prop_zip_file" >> $logfile 2>&1

    echo -e "\nRunning (as $appsudo): cp -pf $war_file $prop_zip_file /app/proj/$INSTANCE/webapps" | tee -a $logfile
    sudo -u $appsudo sh -c "cp -pf $war_file $prop_zip_file /app/proj/$INSTANCE/webapps" >> $logfile 2>&1
    if [ $? -ne 0 ]
    then
        echo -e "\nThere was a problem executing sudo -u $appsudo sh -c \"cp -pf $war_file $prop_zip_file /app/proj/$INSTANCE/webapps\"" | tee -a $logfile
        failemail
    fi #

    if [ -f "/app/proj/$INSTANCE/webapps/$war_file" ] &&  [ -f "/app/proj/$INSTANCE/webapps/$prop_zip_file" ]
    then
        echo -e "\nThe war file and prop zip file were successfully copied to the /app/proj/$INSTANCE/webapps folder." | tee -a $logfile
        echo -e "\nUnzipping the prop zip file and adding it to the war file within the /app/proj/$INSTANCE/webapps folder." | tee -a $logfile

        # Now unzip the properties zip file and add it to the war file.
        cd /app/proj/${INSTANCE}/webapps

        echo -e "\nRunning (as $appsudo): unzip $prop_zip_file" | tee -a $logfile
        sudo -u $appsudo sh -c "unzip $prop_zip_file" >> $logfile 2>&1
     
        echo -e "\nRunning (as $appsudo): zip -rm $war_file js" | tee -a $logfile
        sudo -u $appsudo sh -c "zip -rm $war_file js" >> $logfile 2>&1

        echo -e "\nRunning (as $appsudo): zip -rm $war_file WEB-INF" | tee -a $logfile
        sudo -u $appsudo sh -c "zip -rm $war_file WEB-INF" >> $logfile 2>&1

        if [ $? -ne 0 ]
        then
            echo -e "\nAdding the contents of the zip file to the war files was not successful." | tee -a $logfile
            failemail
        else
            echo -e "\nThe contents of the zip file were successfully added to the war file." | tee -a $logfile
            echo -e "\n\nSuccess running $action for $project" | tee -a $logfile
        fi
    else
        echo -e "\nThe war file and prop zip file were not successfully copied to the /app/proj/$INSTANCE/webapps folder." | tee -a $logfile
        failemail
    fi
;;

#-----------------------------------------------------------------------------------------------------------------#
#                                          UNDEPLOY WAR/EAR ON THE APP SERVER                                     #
#-----------------------------------------------------------------------------------------------------------------#
undeploy)
    echo -e "\nRunning (as $tcsudo): $tcadmin  undeploy  $INSTANCE -app  $war_file\n" | tee -a $logfile 
    sudo -u  $tcsudo  $tcadmin  undeploy  $INSTANCE -app  $war_file >> $logfile 2>&1
    if [ $? -eq 0 ] 
    then
      echo -e "\nSuccess running $action for $project" | tee -a $logfile
    else
      echo -e "\nFailure running $action for $project" | tee -a $logfile
      failemail
    fi
;;

#-----------------------------------------------------------------------------------------------------------------#
#                                           DEPLOY WAR/EAR ON THE APP SERVER                                      #
#-----------------------------------------------------------------------------------------------------------------#
deploy)
    echo -e "\nRunning (as $tcsudo): $tcadmin  deploy  $INSTANCE -app  $war_file\n" | tee -a $logfile
    sudo -u  $tcsudo  $tcadmin  deploy  $INSTANCE  -app  $war_file >> $logfile 2>&1
    if [ $? -eq 0 ]
    then
        echo -e "\nSuccess running $action of $INSTANCE for $project" | tee -a $logfile
    else
        echo -e "\nFailure running $action of $INSTANCE for $project" | tee -a $logfile
        failemail
    fi
;;

#-----------------------------------------------------------------------------------------------------------------#
#                                                 STOP APP INSTANCE                                               #
#-----------------------------------------------------------------------------------------------------------------#
stop)
    echo -e "\nRunning (as $tcsudo): $tcadmin  stop  $INSTANCE\n" | tee -a $logfile 
    sudo -u  $tcsudo  $tcadmin  stop  $INSTANCE >> $logfile 2>&1
    if [ $? -eq 0 ]
    then
        echo -e "\nSuccess running $action for $project" | tee -a $logfile
    else
        echo -e "\nFailure running $action for $project" | tee -a $logfile
        failemail
    fi
;;

#-----------------------------------------------------------------------------------------------------------------#
#                                                 START APP SERVER                                                #
#-----------------------------------------------------------------------------------------------------------------#
start)
    echo -e "\nRunning (as $tcsudo): $tcadmin  start  $INSTANCE\n" | tee -a $logfile 
    sudo -u  $tcsudo  $tcadmin  start  $INSTANCE >> $logfile 2>&1
    if [ $? -eq 0 ]
    then
        echo -e "\nSuccess running $action for $project" | tee -a $logfile
    else
        echo -e "\nFailure running $action for $project" | tee -a $logfile
        failemail
    fi
;;

*)
    echo -e "\nPlease provide a valid option\nValid options are : stage/undeploy/deploy/stop/start" | tee -a $logfile
    failemail
;;

esac

#-----------------------------------------------------------------------------------------------------------------#
#                                        SEND SUCCESSFULL DEPLOY NOTIFICATION                                     #
#-----------------------------------------------------------------------------------------------------------------#
echo -e "----------------------------------"   | tee -a $logfile
echo -e "\nCompleted Action: $action"          | tee -a $logfile
echo -e "\n----------------------------------" | tee -a $logfile

passemail

exit 0     # ---- Congratulations: This job succeeded!!! ------ #
