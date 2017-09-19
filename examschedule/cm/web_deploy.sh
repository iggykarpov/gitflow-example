#!/bin/bash
#---------------------------------------------------------------------------------------------------------------------#
# AUTHOR        : Jay.Renbaum@finra.org                                                                               #
# VERSION       : 1.0                                                                                                 #
# DESCRIPTION   : This script is used to automate deployment of the webapp.zip file to the apache servers.            #
#---------------------------------------------------------------------------------------------------------------------#

DATE=`date +%m%d%Y%H%M%S`
tcsudo=springs
notify="Rene.Arellano@finra.org,Ajith.Puppala@finra.org,Kaitlyn.Reese@finra.org,Senthil.Sugumar@finra.org,Alex.Tsirel@finra.org,Vasily.Ruzha@finra.org,Saurabh.Varma@finra.org,Vivian.Lewy@finra.org"
#notify="Jay.Renbaum@finra.org"

#-----------------------------------------------------------------------------------------------------------------#
#                                       MAIN SECTION OF SCRIPT                                                    #
#-----------------------------------------------------------------------------------------------------------------#

# process paramaters passed in by BladeLogic
if [ $# -ne 2 ]
then
    # Currently, there is only one action performed by this script. As this may change, the action is still being passed in by BladeLogic
    echo -e "\nUsage: $0 targetenv action"
    echo -e "\nExamples:\n$0 dev deploy\n0 qc deploy\n" 
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
        echo -e "\nUnknown targetenv: $targetenv. Use dev/lab/int/qc/qc2/qcpm/qcpm2/train/prod/prod2"
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
        elif [ "$component" = "EP_SCHEDULE" ]
	then
            export c_abbr="esched"
        else
            echo -e "\nComponent value found in buildinfo file was not expected: \"${component}\". Exiting script." 
            exit 1
        fi
    fi

    # Now that we know the targetenv and the component value, we can set the js_zip variable 
    js_zip="${c_abbr}-${targetenv}.zip"  

    # Initialize the logfile and emailfile variables now that we know the component value
    logfile="/tmp/$c_abbr.$1.$2.$DATE.$$.txt"
    emailfile="/tmp/$c_abbr.$1.$2.$DATE.$$"

    # Delete any versions of the logfile or emailfile that may exist from a previous deploy attempt for the same build
    if [ -f "$emailfile" ] ; then rm $emailfile ; fi
    if [ -f "$logfile" ] ; then rm $logfile ; fi

    # Delete old emailfiles and logfiles for this particular targetenv and action
    find /tmp -name "$c_abbr.$1.$2*" -type f -mtime +1 -exec rm -f '{}' \; >/dev/null 2>&1

    echo -e "----------------------------------"   | tee -a $logfile
    echo -e "\nStarting Action: $action"           | tee -a $logfile
    echo -e "\n----------------------------------" | tee -a $logfile
    echo -e "\nDate           : `date`"            | tee -a $logfile
    echo -e "Component      : $component"          | tee -a $logfile 
    echo -e "Component abbr : $c_abbr"             | tee -a $logfile 
    echo -e "PATH           : $PATH"               | tee -a $logfile 

    #-----------------------------------------------------------------------------------------------------------------#
    #                                VALIDATE ADDITIONAL REQUIRED DEPLOYMENT PARAMETERS                               #
    #-----------------------------------------------------------------------------------------------------------------#

    export project=`grep "Project:" buildinfo | awk ' { print $NF } '`                         
    if [ -z "$project" ]  ; then echo "project not set, exiting ..."  ; exit 1 ; else echo -e "Project        : $project"  | tee -a $logfile ; fi

    export application=`grep "Application:" buildinfo | awk ' { print $NF } '`                         
    if [ -z "$application" ]  ; then echo "application not set, exiting ..."  ; exit 1 ; else echo -e "Application    : $application"  | tee -a $logfile ; fi
    export buildid=`grep "CM Label:" buildinfo | awk ' { print $NF } '`
    if [ -z "$buildid" ]  ; then echo "buildid not set" ; else echo -e "Buildid        : $buildid" | tee -a $logfile ; fi

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

# Obtain apache_root_path for the current environment:
eval `grep "${TARGETENV_UPPER}:" $propertyfile | eval sed 's/${TARGETENV_UPPER}://g'`
if [ -z "$apache_root_path" ] ; then 
    echo "apache_root_path not set, exiting ..." 
    exit 1 
else 
    echo -e "\napache root : $apache_root_path" | tee -a $logfile 
fi

# Obtain the name of the webapp.zip file:
eval `grep "GLOBAL:" $propertyfile | eval sed 's/GLOBAL://g'`

echo -e "webapp zip  : $WEBAPP_FILE" | tee -a $logfile
echo -e "host        : `hostname`"   | tee -a $logfile
echo -e "osinfo      : `uname`"      | tee -a $logfile
echo -e "targetenv   : $targetenv"   | tee -a $logfile
echo -e "logfile     : $logfile"     | tee -a $logfile

echo -e "\n----------------------------------" | tee -a $logfile

#-----------------------------------------------------------------------------------------------------------------#
#                          SENDMAIL SUBROUTINES FOR SENDING SUCCESS AND FAILURE EMAILS                            #
#-----------------------------------------------------------------------------------------------------------------#

function passemail {

    SUBJECT="SUCCESS : $project : $component $targetenv apache $action message for buildid: $buildid"
    echo -e "From: $LOGNAME\nTo: $notify\nSubject: $SUBJECT\nMime-Version: 1.0\nContent-Type: text/html\n" > $emailfile
    echo "<html>" >> $emailfile
    echo "<body>" >> $emailfile
    echo "<pre>" >> $emailfile
    awk ' { print $0 } ' $logfile | sed -e 's/Success running .*/<div align=\"center\" style=\"background: green ; font-size: 24px\">SUCCESS<\/div>/g' >> $emailfile
    echo "</pre>"  >> $emailfile
    echo "</body>" >> $emailfile
    echo "</html>" >> $emailfile
    sendmail -t -oi < $emailfile
}

function failemail {
  
    SUBJECT="FAILURE : $project : $component $targetenv apache $action message for buildid: $buildid"
    echo -e "From: $LOGNAME\nTo: $notify\nSubject: $SUBJECT\nMime-Version: 1.0\nContent-Type: text/html\n" > $emailfile
    echo "<html>" >> $emailfile
    echo "<body>" >> $emailfile
    echo "<pre>" >> $emailfile
    awk ' { print $0 } ' $logfile | sed -e 's/Failure running.*/<div align=\"center\" style=\"background: red ; font-size: 24px\">FAILURE<\/div>/g' >> $emailfile
    echo "</pre>"  >> $emailfile
    echo "</body>" >> $emailfile
    echo "</html>" >> $emailfile
    sendmail -t -oi < $emailfile

    exit 1
}

#-----------------------------------------------------------------------------------------------------------------#
#                                         START CREATION OF DEPLOYMENT ACTIONS                                    #
#-----------------------------------------------------------------------------------------------------------------#

case "$action" in 

deploy)
    #-------------------------------------------------------------------------------------------------------------#
    #                                          DEPLOY ZIP FILE ON THE WEB SERVER                                  #
    #-------------------------------------------------------------------------------------------------------------#

    # Make sure the apache root path exists on this machine before attempting to deploy the static files.
    if [ -d "$apache_root_path" ] ; then
        echo -e "\nThe apache root path exists as expected: $apache_root_path" | tee -a $logfile

        # Check for component folder in the apache_root_path
        if [ -d "$apache_root_path/$c_abbr" ] ; then
            echo -e "\nThe $c_abbr directory exists under the apache root dir." | tee -a $logfile
            # component folder does exist (expected). Delete it so that we don't have to worry about old static files 
            # after the subsequent unzip is run
            echo -e "\nRunning (as $tcsudo): rm -rf $apache_root_path/$c_abbr\n" | tee -a $logfile
            sudo -u  $tcsudo rm -rf $apache_root_path/$c_abbr >> $logfile 2>&1

            # Check for dir existence again to validate that the rm command worked
            if [ -d "$apache_root_path/$c_abbr" ] ; then
                echo -e "\nPath to component folder exists after attempted removal: $apache_root_path/$c_abbr." | tee -a $logfile
                echo -e "\nFailure running $action of static files for $project" | tee -a $logfile
                failemail
            else
                # Unzip the zip file to the apache root dir
                echo -e "The child folder $c_abbr was deleted successfully. " | tee -a $logfile
                echo -e "\nRunning (as $tcsudo): unzip -qo $WEBAPP_FILE -d ${apache_root_path}/\n" | tee -a $logfile
                sudo -u  $tcsudo unzip -qo $WEBAPP_FILE -d ${apache_root_path}/ >> $logfile 2>&1
            fi
        else
            # The component directory does not exist which is ok (first time through?); simply unzip to the apache root as 
            # the zip file contains the component name in the path 
            echo -e "\nRunning (as $tcsudo): unzip -qo $WEBAPP_FILE -d $apache_root_path/\n" | tee -a $logfile
            sudo -u  $tcsudo unzip -qo $WEBAPP_FILE -d $apache_root_path/ >> $logfile 2>&1
       fi
    else
        echo -e "\nThe apache root path does not exist: $apache_root_path" | tee -a $logfile
        echo -e "\nFailure running $action of static files for $project" | tee -a $logfile
        failemail
    fi

    # Check status of first unzip command 
    if [ $? -eq 0 ]
    then
        echo -e "The $WEBAPP_FILE file was unzipped successfully. " | tee -a $logfile
        # First unzip was successful, now lets unzip the config.js file to the $c_abbr/js folder to overwrite the 
        # current file with the file found in the environment specific zip file. 
        
        echo -e "\nRunning (as $tcsudo): unzip -qo $js_zip "js/config.js" -d ${apache_root_path}/${c_abbr}/\n" | tee -a $logfile
        sudo -u  $tcsudo unzip -qo $js_zip "js/config.js" -d ${apache_root_path}/${c_abbr}/ >> $logfile 2>&1

        # Check status of second unzip command 
        if [ $? -eq 0 ]
        then
            echo -e "The $js_zip file was unzipped successfully. " | tee -a $logfile
            echo -e "\nSuccess running $action of static files for $project" | tee -a $logfile
        else            
            if [ "$component" != "EP_SCHEDULE" ]
            then
                echo -e "\nThe unzip of the $js_zip file failed." | tee -a $logfile
                echo -e "\nFailure running $action of static files for $project" | tee -a $logfile
            	failemail
            else
                echo -e "\nSkipped unzip status check for $js_zip for component $component" | tee -a $logfile
            fi
        fi
    else
        echo -e "\nThe unzip of the $WEBAPP_FILE file failed." | tee -a $logfile
        echo -e "\nFailure running $action of static files for $project" | tee -a $logfile
        failemail
    fi
;;

*)
    echo -e "\nPlease provide a valid option\nValid options are : deploy" | tee -a $logfile
    failemail
;;

esac

#-----------------------------------------------------------------------------------------------------------------#
#                                        SEND SUCCESSFULL DEPLOY NOTIFICATION                                     #
#-----------------------------------------------------------------------------------------------------------------#
echo -e "\n----------------------------------" | tee -a $logfile
echo -e "\nCompleted Action: $action"          | tee -a $logfile
echo -e "\n----------------------------------" | tee -a $logfile

passemail

exit 0     # ---- Congratulations: This job succeeded!!! ------ #
