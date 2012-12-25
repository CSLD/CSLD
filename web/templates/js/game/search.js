jQuery(document).ready(function(){
    jQuery("#searchButton").bind('click', function(){
        jQuery.ajax({
            url: "/game/searchAjax.jsp",
            data: {
                searchQuery: jQuery("#searchQuery").val(),
                queryTarget: jQuery("#queryTarget option:selected").val()
            },
            type: "POST"
        }).done(function (data){
                jQuery("#games").remove();
                jQuery("div#content").append(data);
            });
    });
});