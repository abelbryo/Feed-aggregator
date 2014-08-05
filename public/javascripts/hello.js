$(document).ready(function() {

    $("#title-search").submit(function(e) {
        $("#response-box").html("Your search results.");

        var MY_URL = window.location.origin + "/searchbykeyword";

        var serliazedFormData = $(this).serializeArray();
        var jsonData = {};
        var jsonFormData = $.map(serliazedFormData, function(elem) {
            jsonData[elem.name] = elem.value;
        });

        var str_data = JSON.stringify(jsonData);

        $.ajax({
            url: MY_URL,
            type: "POST",
            dataType: "json",
            contentType: "application/json",
            data: str_data

        }).done(function(responseArray) {
            window.myResp = responseArray;
            console.debug("%c%s", "background:deepskyblue;color:red;font-size:34px", str_data);
            if (responseArray.status)
                $("#response-box").html(responseArray.status);
            else
                $.map(responseArray, function(elem) {
                    $("#response-box").append("<li>" + elem.title + "<br />" +
                        elem.description + "<br />" +
                        elem.link + "<br />" +
                        new Date(elem.pubDate) + "<br /> </li>");
                });

        }).fail(function(xhr, textStatus, error) {
            $("#response-box").text(xhr.responseText);
            console.log("REQUEST " + xhr.responseText);
            console.log("STATUS " + textStatus);
            console.log("ERROR " + error);
        });
        e.preventDefault(); // STOP default action
    });


    // Footer date
    $(".footer-current-year").text(new Date().getFullYear());


});
