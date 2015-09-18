/**
 * Initialize carousel
 */
var owl = $("#${carouselId}");

owl.owlCarousel({
    items : 1,
    lazyLoad : true,
    autoPlay : false,
    stopOnHover: true,
    navigation : false,
    singleItem: true
});

$('.owl-carousel .fa-chevron-circle-right').click(function(){
    owl.trigger('owl.next');
});

$('.owl-carousel .fa-chevron-circle-left').click(function(){
    owl.trigger('owl.prev');
});
