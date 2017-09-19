var componentmapping = (function() {
    //var typeSubTypeMap = {};
    var mapDataTable;
	return {
		init: function() {
            $("#container").hide();
            $('#loading').show();
            $.ajax({
                type: "POST",
                url: "/esched/services/PS/checkUserPermissions",
                cache: false,
                async: true,
                dataType: "xml"
            }).done(function( oXML ) {
                $("#loading").hide();
                oXML = $(oXML);
                var oUserPerm = oXML.find("userpermissions");
                var returncode=oUserPerm.attr("status");
                if (returncode !== "1") {
                    reportError(oUserPerm);
                    return false;
                }

                var hasCompMappingPermission = oUserPerm.find("componentmapping").length && oUserPerm.find("componentmapping").text() == "Y";

                if(hasCompMappingPermission){
                    componentmapping.loadData();
                    $("#container").show();
                }else{
                    $("#container").remove();
                    EXAM.Error.create({"error" : 'You do not have permission to view this page.'});
                }

            }).fail(function( jqXHR, textStatus ) {
                $('#loading').hide();
                $("#container").remove();
                EXAM.Error.create({"error" : 'Request failed.'});
            });

		},
        loadData: function(){
            $('#loading').show();
            $.ajax({
                type: "GET",
                url: "services/PS/getTypeSubTypeMappings",
                cache: false,
                async: true,
                dataType: "json"
            }).done(function( oData ) {

                $('#loading').hide();
                if (oData["status"] !== 1) {
                    reportError(oData);
                    return false;
                }

                var compConfig = componentmapping.getComponentConfig();
                var dataSet = [];
                var columnInfo = [];
                var items = oData.mapping;

                $.each(compConfig, function( index, conf ) {
                    var def = {
                        "data": conf.key,
                        "title": conf.title,
                        "targets": [index]
                    };
                    if(conf.type =="checkbox") {
                        def["render"] = function ( data, type, row ) {
                            return '<div class="checkboxWrapper"><span class="checkboxVal">'+data+'</span><input name="'+conf.key+'" type="checkbox" ' + (data =="Y" ? 'checked': '') + '/></div>';
                        }
                    }else if(conf.type =="checkbox-readonly"){
                        def["render"] = function(data, type, row){
                            return (data == 'Y' ? '<div class="checkboxWrapper"><i class="fa fa-check checkbox-readonly" aria-hidden="true"></i></div>' : '');
                        }
                    }else if(conf.type == "date"){
                        def["render"] = function(data, type, row){
                            return $.format.date(data, 'MM/dd/yy');
                        }
                        def["type"] = "num";
                    }else if(conf.type){
                        def["type"] = conf.type;
                    }

                    columnInfo.push(def);
                });
                //console.log("columnInfo: ", columnInfo)


                for (var i = 0; i < items.length; i++) {
                    var oChild = items[i];
                    if(oChild) {
                        var row = [];
                        $.each(compConfig, function (index, conf) {
                            var val = oChild[conf.key];
                            row.push(val);
                        });
                        oChild["DT_RowId"] = "row_" + oChild["typeSubTypeCode"] + "_" + oChild["examTypeSubTypeCode"];
                        dataSet.push(oChild);
                        //typeSubTypeMap[oChild["examTypeSubTypeCode"]] = oChild;
                    }
                }

                componentmapping.renderGrid({dataSet: dataSet,columnInfo: columnInfo});

            }).fail(function( jqXHR, textStatus ) {
                $('#loading').hide();
                EXAM.Error.create({"error" : 'Request failed.'});
            });
        },
        renderGrid: function(data){
            var dataSet = data.dataSet;

            mapDataTable = $('#componentMappingTable').DataTable( {
                data: dataSet,
                "scrollY": "calc(100vh - 180px)",
                "scrollX": true,
                "scrollCollapse": true,
                "paging": false,
                "columns": data.columnInfo,
                "orderFixed": [0, "asc"],
                "drawCallback": function( settings ) {
                    $('#loading').hide();
                    $("#componentMappingTable input[type=checkbox]").unbind("click").on("click", function(){
                        $(this).closest("tr").addClass("modified");
                        $(this).addClass("modified");
                    });
                },
                "initComplete": function(settings, json) {
                    var timeout;
                    $(".searchFld").val("");
                    $(".searchFld").unbind("keyup").on("keyup", function(){
                       var searchStr = $(this).val();
                       if(timeout) clearTimeout(timeout);
                       timeout = setTimeout(function(){
                           mapDataTable.search(searchStr).draw();
                       }, 400);
                    });

                    var descColumn = this.api().column(2); // description column
                    var selOptions = [];
                    descColumn.data().unique().sort().each( function ( d, j ) {
                        selOptions.push({value:d, text:d});
                    } );

                    var dropdown = $('#subTypeFilter').selectize({
                        create: false,
                        sortField: [{field: 'text',direction: 'asc'}, {field: '$score'}],
                        options: selOptions,
                        placeholder: "Select Exam Sub-Type",
                        onChange: function(value){
                            //var val = $.fn.dataTable.util.escapeRegex(value);

                           // descColumn.search( val ? '^'+val+'$' : '', true, false).draw();
                            descColumn.search(value).draw();
                            //mapDataTable.search(value).draw();
                        }
                    });
                }
            } );


            $('#grid th:nth-child(1)').on("click",function() {
                var order = mapDataTable.order();
                mapDataTable.order.fixed({
                   pre: [0, order[0][1]]
                });
                mapDataTable.draw();

            });

        },
        resetFilters: function(){
            $('#loading').show();
            $(".searchFld").val("");
            setTimeout(function() {
                $("#subTypeFilter")[0].selectize.setValue(null);
                mapDataTable.search('').draw();
            }, 200);
        },
        save: function(){
            var data = [];
            $("#componentMappingTable tr.modified").each(function(){
                var rowId = $(this).attr('id');
                var rowData = mapDataTable.row('#' + rowId).data();
                //console.log("data: ", rowData);

                var mappingObj = {
                    "typeSubTypeComponentMapId": rowData.typeSubTypeComponentMapId,
                    "typeSubTypeCode": rowData.typeSubTypeCode,
                    "examTypeSubTypeCode": rowData.examTypeSubTypeCode
                };
                $(this).find("input[type=checkbox]").each(function(){
                    mappingObj[$(this).attr("name")] = ($(this).prop("checked") ? "Y" : "N");
                });
                data.push(mappingObj);
            })

            //console.log("data saved: " + JSON.stringify(data));
            $('#loading').show();
            $.ajax({
                type : "POST",
                url : "services/PS/saveOrUpdateTypeSubTypeMappings",
                data : JSON.stringify(data),
                cache : false,
                async : true,
                contentType: 'application/json',
                dataType : 'json'
            }).done(function(data) {

                if (!data || data["status"] == "-1") {
                    EXAM.Error.create({"error" : 'Request failed'});
                    return;
                }
                var items = data.mapping;

                for (var i = 0; i < items.length; i++) {
                    var oChild = items[i];
                    if(oChild) {
                        var typeSubTypeCode = oChild["typeSubTypeCode"];
                        var examTypeSubTypeCode = oChild["examTypeSubTypeCode"];

                        //update rows
                        mapDataTable.row('#row_' + typeSubTypeCode + "_" + examTypeSubTypeCode).data(oChild);
                        $('#componentMappingTable .modified').removeClass('modified');
                    }
                }
                mapDataTable.draw();
                $('#loading').hide();

            }).fail(function(jqXHR, textStatus) {
                    $('#loading').hide();

                    EXAM.Error.create({
                        "error" : 'Request failed. ' + textStatus
                    });

                }
            );

        },
        getComponentConfig: function(){
            var map = [
                {title: "Type", key: "typeSubTypeCode"},
                {title:"Code", key:"examTypeSubTypeCode", type: "num"},
                {title:"Description", key:"examTypeSubTypeDescription"},
                {title:"Type Id", key:"matterTypeSubTypeId", width: "50px"},
                {title:"Active", key:"activeFlag", type: "checkbox-readonly"},
                {title:"SP", key:"spFlag", type:"checkbox"},
                {title:"MUNI", key:"municipalFlag", type:"checkbox"},
                {title:"MUNI ADV", key:"municipalAdvisorFlag", width:"55px", type:"checkbox"},
                {title:"OPTIONS", key:"optionFlag",type:"checkbox"},
                {title:"RSA SP", key:"rsaSpFlag", width:"50px", type:"checkbox"},
                {title:"SDF", key:"sdfFlag", type:"checkbox"},
                {title:"FIRST FN", key:"firstFinopFlag", width:"50px", type:"checkbox"},
                {title:"FINOP", key:"finopFlag", type:"checkbox"},
                {title:"RSA FN", key:"rsaFinopFlag", width:"50px", type:"checkbox"},
                {title:"ANC", key:"ancFlag",  type:"checkbox"},
                {title:"FLOOR", key:"floorFlag",  type:"checkbox"},
                {title:"MORE", key:"moreComponentsFlag", type:"checkbox"},
                {title:"Last Updated On", key:"lastUpdateDate", width:"100px", type:"date"},
                {title:"Last Updated By", key:"userFullName", width:"100px"}
            ];
            return map;
        }
	};
})();

$(function() {
	componentmapping.init();
});

