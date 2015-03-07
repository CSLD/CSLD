/**
 * Initialization javascript for OWL panel
 */
$("#${carouselId}").owlCarousel({
    items : 1,
    lazyLoad : true,
    autoPlay : 12000,
    stopOnHover: true,
    navigation : false,
    singleItem: true,
    beforeInit : function(elem){
        elem.children().sort(function(){
            return Math.round(Math.random()) - 0.5;
        }).each(function(){
            $(this).appendTo(elem);
        });
    }
});

//Sort random function
function random(owlSelector){
}
