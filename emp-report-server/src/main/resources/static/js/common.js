/**
 * Created by Charley on 2017/5/11.
 */
$(document).ready(function () {

    //绑定工具栏事件处理
    $("#btn_first_page").on("click", handlerFirstPage);
    $("#btn_pre_page").on("click", handlerPrePage);
    $("#btn_next_page").on("click", handlerNextPage);
    $("#btn_last_page").on("click", handlerLastPage);
    $("#txt_page_index").on("change", handlerJumpPage);
    $("#btn_print_do").on("click",handlerPrintReport);

    var reportKey = $("#hidden_report_key").val();
    var pageIndex = Number($("#hidden_page_index").val());
    var pageCount = Number($("#hidden_page_count").val());
    $("#txt_page_index").val(pageIndex + 1);
    $("#txt_page_count").html(pageCount);
    if (reportKey) {
        //加载报表内容
        loadReportContent(reportKey);
    }
});

function loadReportContent(reportKey, pageIndex) {
    $("#div_mask").show();
    $("#report_content").hide();
    //加载报表主体内容
    var url;
    if (pageIndex) {
        url = "/report_server/html?report_key=" + reportKey + "&page_index=" + pageIndex;
    }
    else {
        url = "/report_server/html?report_key=" + reportKey;
    }
    $.get(url, function (data) {
        if (!data) {
            return;
        }
        pageIndex = data.pageIndex;
        var pageCount = data.pageCount;
        var content = data.content;
        $("#hidden_page_index").val(pageIndex);
        $("#hidden_page_count").val(pageCount);
        $("#txt_page_index").val(pageIndex + 1);
        $("#txt_page_count").html(pageCount);
        $("#report_content").html(content);
        $("#report_content").show();
        $("#div_mask").hide();
        //mergeCells();
    });
}

function handlerFirstPage() {
    var reportKey = $("#hidden_report_key").val();
    var pageIndex = 0;
    loadReportContent(reportKey, pageIndex);
}

function handlerPrePage() {
    var reportKey = $("#hidden_report_key").val();
    var pageIndex = Number($("#hidden_page_index").val());
    if (pageIndex > 0) {
        pageIndex--;
        loadReportContent(reportKey, pageIndex);
    }
}

function handlerNextPage() {
    var reportKey = $("#hidden_report_key").val();
    var pageIndex = Number($("#hidden_page_index").val());
    var pageCount = Number($("#hidden_page_count").val());
    if (pageIndex + 1 < pageCount) {
        pageIndex++;
        loadReportContent(reportKey, pageIndex);
    }
}

function handlerLastPage() {
    var reportKey = $("#hidden_report_key").val();
    var pageCount = Number($("#hidden_page_count").val());
    if (pageCount > 0) {
        var pageIndex = pageCount - 1;
        loadReportContent(reportKey, pageIndex);
    }
}

function handlerJumpPage(e) {
    var reportKey = $("#hidden_report_key").val();
    var pageIndex = Number(e.currentTarget.value);
    pageIndex--;
    var pageCount = Number($("#hidden_page_count").val());
    if (pageIndex < pageCount) {
        loadReportContent(reportKey, pageIndex);
    }
}

function mergeCells() {
    /*合并同值单元格*/
    var tdDiv = $("#report_content table tr td:nth-child(3)[data-sequence-ext='1'] div");
    var firstDiv = "";
    var currentDiv ="";
    var rowNumber = 0;
    tdDiv.each(function(i){
       if(i==0){
           firstDiv = $(this);
           rowNumber = 1;
       }else{
           currentDiv = $(this);
           if(firstDiv.text()==currentDiv.text()){
               rowNumber++;
               currentDiv.parent("td").remove();
               firstDiv.parent("td").attr("rowspan",""+rowNumber)
           }else{
               firstDiv = $(this);
               rowNumber = 1;
           }
       }
    })
}

function handlerPrintReport(){
    /*打印报表*/
    var oldStr = document.body.innerHTML;
    $("body").html($("#report_content").html());
    window.print();
    document.body.innerHTML = oldStr;
    return false;
}
