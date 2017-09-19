#!/bin/nsh
################################################################################
#
#  Script Name:  bl_EXAMWORKSPACE_EP_CLOSE_V1_Driver.nsh
#  Locations:    /FINRA/MEMBER_REG/EXAMWORKSPACE
#  Description:  Script is used to create Custom Software Packages and Jobs for this Application
#
################################################################################

#-------------------------------------------------------------------------------
#   Set VARs for Application and Tower
#-------------------------------------------------------------------------------
TOOLDIR=`dirname $0`
ROOT="FINRA"
TOWER="MEMBER_REG"
APP="EXAMWORKSPACE"
COMPONENT="EP_CLOSE"
COMPONENT_ABBR="ec"

#-------------------------------------------------------------------------------
#  Function to Get Global Functions from BladeLogic
#-------------------------------------------------------------------------------
getFunctions()
{
    local fileName="${1}"
    local groupName="${2}"
    blcli_execute DepotFile getLocationByGroupAndName "${groupName}" "${fileName}"
    blcli_storeenv LIBFILE
    if [ -r ${LIBFILE} ] ; then
        echo "Importing BL API function library"
        . ${LIBFILE}
    else
        echo "BL API function library $LIBFILE can not be accessed.  Exiting."
        exit 1
    fi

    initTmpFile
    setNull
    DEBUG="true"
}

#-------------------------------------------------------------------------------
#  Function to Print Usage Message
#-------------------------------------------------------------------------------
print_usage()
{
    echo ""
    echo "  -n  <name>       - name of the software, may include release, iteration, build id, etc"
    echo "  -e  <env>        - name of the promotion state (DEV, QC, PROD) this is being deployed to"
    echo "  -s  <source>     - NSH path to the software being deployed"
	echo "  -a  <autodeploy> - auto deployment environments"
    echo ""
    exit 1
}

is_new_arg()
{
    print_debug "Checking ${1} to see if it's an argument or option..."
    echo "${1}" | egrep ^-n\|^-e\|^-s
    RES=$?

    if [ -z $? ] ; then
        RES=0
    fi

    return $RES
}

parse_args()
{
    print_debug "Arguments: $@"

    if [ $# -ge 2 ] ; then
        while [ $# -ge 2 ] ; do
            case "${1}" in
            -n)
                shift
                if ! is_new_arg "${1}" ; then
                    APP_NAME="${1}"
                fi
                shift
                ;;
            -e)
                shift
                if ! is_new_arg "${1}" ; then
                    TARGET_ENV="${1}"
                fi
                shift
                ;;
            -s)
                shift
                if ! is_new_arg "${1}" ; then
                    SOURCE="${1}"
                fi
                shift
                ;;
            -a) 
	            shift
				if [ "${1}" = "-autodeploy" ] ; then
					AUTO_DEPLOY="true"
					shift
			    else
			        AUTO_DEPLOY="false"
			    fi
                ;;
            *)
                print_usage
                ;;
            esac
        done
    fi
}

################################################################################
#   Job Creation Subroutines
################################################################################

SUB_APP()
{
    OSTYPE="Linux"

    echo "\n** IN APP SUBROUTINE"
    echo "\n**SOURCE = ??SOURCE??"
    echo "\n**DEPLOYPATH = ??DEPLOYPATH??"

    LOG_FILE="/tmp/${TARGET_ENV}.${APP}.${TIMESTAMP}.LOGFILE"
    MAIL_TO="Jay.Renbaum@finra.org,Rene.Arellano@finra.org,Venkata.Valluri@finra.org,Todd.Griffiths@finra.org"
    MAIL="mail"
    echo "\n**LOG_FILE = ${LOG_FILE}"

    #--------------------------------------
    # Stage the war file 
    #--------------------------------------
    SOURCE_FOLDER="${COMPONENT_ABBR}_bl_stage_war"
    JOB_NAME=`printf "%02d" $jobNum`"-${APP}-${COMPONENT}-${RELEASE}-${TIMESTAMP}-stage-${CURR_TARGET_LOWER}-to-${ENV_LOWER}"
    INSTALL_CMD="${BASE_CMD} \`pwd\`/deploy.sh ${CURR_TARGET} ${ENV_LOWER} stage 1>&2"
    createCustomSoftwareDeploy "${FOLDER}" "${OSTYPE}" "${SOURCE}/${SOURCE_FOLDER}" "${INSTALL_TYPE}" "${JOB_NAME}" "${JOB_NAME}" "${INSTALL_CMD}" "${UNINSTALLCMD}" "${APP_SERVER}"
    jobKey[$jobNum]=${RESULT} ; ((jobNum=jobNum+1))
    jobKey_TOMCAT[$jobNum_TOMCAT]=${RESULT} ; ((jobNum_TOMCAT=jobNum_TOMCAT+1))
    
    #--------------------------------------
    # Create the tomcat undeploy job 
    #--------------------------------------
    SOURCE_FOLDER="${COMPONENT_ABBR}_bl_scripts"
    JOB_NAME=`printf "%02d" $jobNum`"-${APP}-${COMPONENT}-${RELEASE}-${TIMESTAMP}-undeploy-${CURR_TARGET_LOWER}-in-${ENV_LOWER}"
    INSTALL_CMD="${BASE_CMD} \`pwd\`/deploy.sh ${CURR_TARGET} ${ENV_LOWER} undeploy 1>&2"
    createCustomSoftwareDeploy "${FOLDER}" "${OSTYPE}" "${SOURCE}/${SOURCE_FOLDER}" "${INSTALL_TYPE}" "${JOB_NAME}" "${JOB_NAME}" "${INSTALL_CMD}" "${UNINSTALLCMD}" "${APP_SERVER}"
    jobKey[$jobNum]=${RESULT} ; ((jobNum=jobNum+1))
    jobKey_TOMCAT[$jobNum_TOMCAT]=${RESULT} ; ((jobNum_TOMCAT=jobNum_TOMCAT+1))
    
    #--------------------------------------
    # Create the tomcat deploy job 
    #--------------------------------------
    SOURCE_FOLDER="${COMPONENT_ABBR}_bl_scripts"
    JOB_NAME=`printf "%02d" $jobNum`"-${APP}-${COMPONENT}-${RELEASE}-${TIMESTAMP}-deploy-${CURR_TARGET_LOWER}-in-${ENV_LOWER}"
    INSTALL_CMD="${BASE_CMD} \`pwd\`/deploy.sh ${CURR_TARGET} ${ENV_LOWER} deploy 1>&2"
    createCustomSoftwareDeploy "${FOLDER}" "${OSTYPE}" "${SOURCE}/${SOURCE_FOLDER}" "${INSTALL_TYPE}" "${JOB_NAME}" "${JOB_NAME}" "${INSTALL_CMD}" "${UNINSTALLCMD}" "${APP_SERVER}"
    jobKey[$jobNum]=${RESULT} ; ((jobNum=jobNum+1))
    jobKey_TOMCAT[$jobNum_TOMCAT]=${RESULT} ; ((jobNum_TOMCAT=jobNum_TOMCAT+1))

    #--------------------------------------
    # Create the job to stop the instance
    #--------------------------------------
    SOURCE_FOLDER="${COMPONENT_ABBR}_bl_scripts"
    JOB_NAME=`printf "%02d" $jobNum`"-${APP}-${COMPONENT}-${RELEASE}-${TIMESTAMP}-stop-${CURR_TARGET_LOWER}-in-${ENV_LOWER}"
    INSTALL_CMD="${BASE_CMD} \`pwd\`/deploy.sh ${CURR_TARGET} ${ENV_LOWER} stop 1>&2"
    createCustomSoftwareDeploy "${FOLDER}" "${OSTYPE}" "${SOURCE}/${SOURCE_FOLDER}" "${INSTALL_TYPE}" "${JOB_NAME}" "${JOB_NAME}" "${INSTALL_CMD}" "${UNINSTALLCMD}" "${APP_SERVER}"
    jobKey[$jobNum]=${RESULT} ; ((jobNum=jobNum+1))
    jobKey_RESTART[$jobNum_RESTART]=${RESULT} ; ((jobNum_RESTART=jobNum_RESTART+1))

    #--------------------------------------
    # Create the job to start the instance
    #--------------------------------------
    SOURCE_FOLDER="${COMPONENT_ABBR}_bl_scripts"
    JOB_NAME=`printf "%02d" $jobNum`"-${APP}-${COMPONENT}-${RELEASE}-${TIMESTAMP}-start-${CURR_TARGET_LOWER}-in-${ENV_LOWER}"
    INSTALL_CMD="${BASE_CMD} \`pwd\`/deploy.sh ${CURR_TARGET} ${ENV_LOWER} start 1>&2"
    createCustomSoftwareDeploy "${FOLDER}" "${OSTYPE}" "${SOURCE}/${SOURCE_FOLDER}" "${INSTALL_TYPE}" "${JOB_NAME}" "${JOB_NAME}" "${INSTALL_CMD}" "${UNINSTALLCMD}" "${APP_SERVER}"
    jobKey[$jobNum]=${RESULT} ; ((jobNum=jobNum+1))
    jobKey_RESTART[$jobNum_RESTART]=${RESULT} ; ((jobNum_RESTART=jobNum_RESTART+1))
}

SUB_WEB()
{
    echo "\nNo content entered for WEB sub-routine"
}

create_batch_job() {

    # This subroutine is called to create batch jobs

    # Arg1: Name of arrary containing jobs.  Do NOT pass the array, just the name.
    # Arg2: Name of the batch job to create.
    # Arg3: Job Group ID of where to create the batch job.
    # Arg4: Name of variable that the batch job key value should be stored in.
    
    if [ ${#} -gt 2 ] ; then
        array_name=${1} ; shift
        batchname="${1}" ; shift
        jobgroupid="${1}" ; shift
        var_name=${1:-BATCH_KEY} ; shift $#
    else
        echo "Not enough parameters were passed to function"
        echo "usage:"
        echo "create_batch_job <name_of_array> <batch_job_name> <job_group_id> [<name_of_var>]"
        echo "  name_of_array:  Name of the array (Do not pass the value using \$array_name) of array that contains list of jobs"
        echo "  batch_job_name: Name of the batch job to create"
        echo "  job_group_id:   Job group ID of where to create the batch job"
        echo "  name_of_var:    Name of variable to store the newly created batch job key"
        return 1
    fi

    echo " "
    echo "Creating: ${batchname} batch job"
    # Get number of elements
    eval NUM_OF_JOBS="\${#${array_name}[*]}"
    echo "Number of jobs:  $NUM_OF_JOBS"
    eval ALL_OF_IT="\${${array_name}[*]}"
    echo "Contents of array:  $ALL_OF_IT"

    index=1
    #NUM_OF_JOBS=${#loc_jobkeys[*]}

    while [ $index -le $NUM_OF_JOBS ] ; do
        eval JOBKEY="\${${array_name}[$index]}"
        echo "JobKey:  $JOBKEY"
        if [ $index -eq 1 ] ; then
            # Create Batch job with first job
            BLCLICMD=(BatchJob createBatchJob "${batchname}" ${jobgroupid} ${JOBKEY} false true false)
            exec_blcli
            batchJobKey="${RESULT}"
        else
            # Add next job to batch job
            BLCLICMD=(BatchJob addMemberJobByJobKey "${batchJobKey}" "${JOBKEY}")
            exec_blcli
            batchJobKey="${RESULT}"
        fi
        (( index=index+1 ))
    done
    eval ${var_name}=${batchJobKey}
}


################################################################################
#   Main Processing
################################################################################

#-------------------------------------------------------------------------------
#   This section needed for running via ANT/CC
#-------------------------------------------------------------------------------
if test -z "$BLCLI_FIRST_TIME" ; then
    LD_LIBRARY_PATH=/usr/nsh/lib
    export LD_LIBRARY_PATH
    BLCLI_FIRST_TIME=false
    export BLCLI_FIRST_TIME
    exec nsh $0 "$@"
fi

#-------------------------------------------------------------------------------
#   This section gets common functions and parses args
#-------------------------------------------------------------------------------
getFunctions "bl_functions.nsh" "/FINRA"
parse_args $@
APP_LOWER=`echo ${APP} | tr "[:upper:]" "[:lower:]"`
COMPONENT_LOWER=`echo ${COMPONENT} | tr "[:upper:]" "[:lower:]"`
DEPOT_TYPE="CUSTOM_SOFTWARE_INSTALLABLE"

# Grab some values from the buildinfo file:
BUILDINFO="${SOURCE}/buildinfo"
REL=`grep "Release:" ${BUILDINFO} | awk '{print $NF}'`
ITERATION=`grep "Iteration:" ${BUILDINFO} | awk '{print $NF}'`
# Combine the two:
RELEASE="$REL-$ITERATION"

# Get all the Environments that Exist, then parse for the right type for this script run
getInstanceNames "Class://SystemObject/${ROOT}/${TOWER}/${APP}/${COMPONENT}/ENVIRONMENT_INSTANCES"
envInstances="${RESULT}"

#-------------------------------------------------------------------------------
#   Loop through each environment and create Jobs/Packages for each
#-------------------------------------------------------------------------------
for envInstance in ${envInstances} ; do
    getPropertyInstanceValue "${envInstance}" ENV_GROUP
    ENV_GROUP="${RESULT}"
    ENVIRONMENT=`basename "${envInstance}"`
    echo "** ENV_GROUP   = ${ENV_GROUP}"
    echo "** ENVIRONMENT = ${ENVIRONMENT}"
    echo "** TARGET_ENV = ${TARGET_ENV}"

    # If the env type for this env is the same as the env type called in the script, and not BUILD do some stuff
    if [ "${ENVIRONMENT}" = "${TARGET_ENV}" ] ; then

        # Get the servers this should be delpoyed to from the Property Dictionary
        getPropertyInstanceValue "${envInstance}" SERVERS
        SERVERS="`echo "${RESULT}" | tr -d '\"' | tr -d '[:space:]' | tr -s ',' ' '`"
        echo "** SERVERS defined as: ${SERVERS}"
        getPropertyInstanceValue "${envInstance}" SERVER_TYPES
        SERVER_TYPES="`echo "${RESULT}" | tr -d '\"' | tr -d '[:space:]' | tr -s ',' ' '`"
        echo "** SERVER_TYPES = ${SERVER_TYPES}"

        for targetType in ${SERVER_TYPES} ; do

            # Get the first server in the list
            target=`echo ${SERVERS} | cut -d" " -f1`

            echo " \n target = ${target}  \n "
            echo " \n targetType = ${targetType}  \n "

            if [ "${targetType}" = "APP" ] ; then
                APP_SERVER=${APP_SERVER},${target}
            fi

            if [ "${targetType}" = "WEB" ] ; then
                WEB_SERVER=${WEB_SERVER},${target}
            fi

            # This line removes the leading server that was just processed (as target) 
            # so that the next server is processed in the next loop.
            SERVERS=`echo ${SERVERS} | cut -d" " -f2-`
        done

        # Not sure what these lines do
        APP_SERVER=${APP_SERVER#,}
        WEB_SERVER=${WEB_SERVER#,}
        
        echo "==========================================="
        echo "** APP_SERVER     = ${APP_SERVER}"
        echo "==========================================="
        echo "** WEB_SERVER     = ${WEB_SERVER}"
        echo "==========================================="
        echo "** TARGET_ENV    = ${TARGET_ENV}"
        echo "** ENV_GROUP     = ${ENV_GROUP}"
        echo "==========================================="


        # get STUPDATE properties from Property Dictionary
        getPropertyInstanceValue "${envInstance}" STU_SRV
        STU_SERVER="${RESULT}"
        getPropertyInstanceValue "${envInstance}" STU_CMD
        STU_COMMAND="${RESULT}"

        # These I can set for all of the deploys
        ACLT="${APP}_MOVE_TO_${ENV_GROUP}"
        TIMESTAMP=`echo ${APP_NAME} | cut -f5 -d-`
        FOLDER_ROOT="/${ROOT}/${TOWER}/${APP}/${COMPONENT}"
        FOLDER="${FOLDER_ROOT}/${RELEASE}/${TIMESTAMP}/${ENVIRONMENT}"
        ENV_LOWER=`echo ${ENVIRONMENT} | tr "[:upper:]" "[:lower:]"`
        UNINSTALLCMD="echo uninstall"

        TARGET_ENV_LOWER=`echo ${TARGET_ENV} | tr "[:upper:]" "[:lower:]"`

        #--------------------------------------
        # Define the INSTALL_TYPE
        #--------------------------------------
        if [ "${ENV_GROUP}" = "DEV" ] ; then
            INSTALL_TYPE="AGENT_COPY_AT_STAGING"
        else
            INSTALL_TYPE="FILE_SERVER"
        fi

        BASE_CMD="chmod -R 775 . ; cd ??SOURCE??  ;  "

        ######################################################
        #                 Start Making jobs                  #
        ######################################################

        # Set up "jobNum" variables for each batch of jobs that will be created.  Start array at 1.  (Korn shell starts at 0, but nsh starts at 1)
        jobNum=1
        jobNum_TOMCAT=1
        jobNum_RESTART=1
        
        # Walk through each of the server types defined in the data dictionary for the current 
        # environment and call the appropriate sub-routine to generate the required jobs.
        for targetType in ${SERVER_TYPES} ; do
        
            # Call subroutines to create jobs for the various server types
            case ${targetType} in
                APP)
                    echo "\n**** Creating jobs for APP server"
                    CURR_TARGET="APP"
                    CURR_TARGET_LOWER=`echo ${CURR_TARGET} | tr "[:upper:]" "[:lower:]"`
                    SUB_APP
                    ;;
                WEB)
                    echo "\n**** Creating jobs for WEB server"
                    CURR_TARGET="WEB"
                    CURR_TARGET_LOWER=`echo ${CURR_TARGET} | tr "[:upper:]" "[:lower:]"`
                    SUB_WEB
                    ;;
                *)
                    echo "\n**** Not a valid server type - ${targetType}!"
            esac

        done

        #--------------------------------------
        # Create job to add ST Deployment Record
        #--------------------------------------
        OSTYPE="Linux"
        JOB_NAME=`printf "%02d" $jobNum`"-${APP}-${COMPONENT}-${RELEASE}-${TIMESTAMP}-STUPDATE-${ENV_LOWER}"
        INSTALL_CMD="${STU_COMMAND}  ??SOURCE??  ${ENVIRONMENT}"
        createCustomSoftwareDeploy  "${FOLDER}"  "${OSTYPE}"  "${SOURCE}/buildinfo"  "${INSTALL_TYPE}"  "${JOB_NAME}"  "${JOB_NAME}"  "${INSTALL_CMD}"  "${UNINSTALLCMD}"  "${STU_SERVER}"
        jobKey[$jobNum]=${RESULT} ; ((jobNum=jobNum+1))

        # Add lines for each batch job that this job should be included in
        jobKey_RESTART[$jobNum_RESTART]=${RESULT} ; ((jobNum_RESTART=jobNum_RESTART+1))
        jobKey_TOMCAT[$jobNum_TOMCAT]=${RESULT} ; ((jobNum_TOMCAT=jobNum_TOMCAT+1))

        ######################################################
        #               Finished Making jobs                 #
        ######################################################

        BLCLICMD=(JobGroup groupNameToId "${FOLDER}")
        exec_blcli
        jobGroupId="${RESULT}"

        #-----------------------------------------------------
        # Create batch jobs 
        #-----------------------------------------------------
        
        create_batch_job jobKey_TOMCAT  "#TOMCAT-DEPLOY-${ENV_LOWER}" "${jobGroupId}" batchJobKeyMaster
        create_batch_job jobKey_RESTART "#RESTART-${ENV_LOWER}" "${jobGroupId}"
        
        #--------------------------------------
        # Apply ACLTemplate to Depot & Job for DATA_LISTENER
        #--------------------------------------
        applyACLTemplateToGroup "${FOLDER}" "DEPOT_GROUP" "${ACLT}" "true" "true"
        applyACLTemplateToGroup "${FOLDER}" "JOB_GROUP" "${ACLT}" "true" "true"

        #--------------------------------------
        # grant SSE deploy privileges to higher environments
        #--------------------------------------
        SSE_ACLT="SSE_DEPLOY_TO_${ENV_GROUP}"
        if [ "${ENV_GROUP}" != "DEV" ] ; then
            applyACLTemplateToGroup "${FOLDER_ROOT}/${RELEASE}" "DEPOT_GROUP" "${SSE_ACLT}" "false" "false"
            applyACLTemplateToGroup "${FOLDER_ROOT}/${RELEASE}/${TIMESTAMP}" "DEPOT_GROUP" "${SSE_ACLT}" "false" "false"
            applyACLTemplateToGroup "${FOLDER}" "DEPOT_GROUP" "${SSE_ACLT}" "true" "false"

            applyACLTemplateToGroup "${FOLDER_ROOT}/${RELEASE}" "JOB_GROUP" "${SSE_ACLT}" "false" "false"
            applyACLTemplateToGroup "${FOLDER_ROOT}/${RELEASE}/${TIMESTAMP}" "JOB_GROUP" "${SSE_ACLT}" "false" "false"
            applyACLTemplateToGroup "${FOLDER}" "JOB_GROUP" "${SSE_ACLT}" "true" "false"
        fi

        #--------------------------------------
        # grant RIM deploy privileges to higher environments
        #--------------------------------------
        RIM_ACLT="RIM_DEPLOY_TO_${ENV_GROUP}"
        if [ "${ENV_GROUP}" != "DEV" ] ; then
            applyACLTemplateToGroup "${FOLDER_ROOT}" "DEPOT_GROUP" "${RIM_ACLT}" "false" "false"
            applyACLTemplateToGroup "${FOLDER_ROOT}/${RELEASE}" "DEPOT_GROUP" "${RIM_ACLT}" "false" "false"
            applyACLTemplateToGroup "${FOLDER_ROOT}/${RELEASE}/${TIMESTAMP}" "DEPOT_GROUP" "${RIM_ACLT}" "false" "false"
            applyACLTemplateToGroup "${FOLDER}" "DEPOT_GROUP" "${RIM_ACLT}" "true" "false"

            applyACLTemplateToGroup "${FOLDER_ROOT}" "JOB_GROUP" "${RIM_ACLT}" "false" "false"
            applyACLTemplateToGroup "${FOLDER_ROOT}/${RELEASE}" "JOB_GROUP" "${RIM_ACLT}" "false" "false"
            applyACLTemplateToGroup "${FOLDER_ROOT}/${RELEASE}/${TIMESTAMP}" "JOB_GROUP" "${RIM_ACLT}" "false" "false"
            applyACLTemplateToGroup "${FOLDER}" "JOB_GROUP" "${RIM_ACLT}" "true" "false"
        fi
        
        #-----------------------------------------------------
        # Auto-deploy
        #-----------------------------------------------------
        if [ "${AUTO_DEPLOY}" = "true" ] ; then     
			echo "** executing deploy to ${TARGET_ENV}"
			BLCLICMD=( Job execute "${batchJobKeyMaster}")
			exec_blcli
			print_result "${RESULT}"
		fi
    fi
done

removeTmpFile


