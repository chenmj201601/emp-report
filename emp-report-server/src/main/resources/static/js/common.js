/**
 * Created by Charley on 2017/5/11.
 */
$(document).ready(function () {
    var report_key = $("#report_key").val();
    if (report_key) {
        //加载报表主体内容
        $.get("/report_server/html?report_key=" + report_key,
            function (data) {
                $("#report_content").html(data);
                $("#div_mask").hide();
            });
    }
});