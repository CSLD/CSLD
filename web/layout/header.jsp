<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Česko-slovenská larpová databáze</title>
    <link rel="shortcut icon" href="img/logo16.ico">
    <link rel="image_src" href="img/logo512.png" />
    <meta name="keywords" content="larp, databáze, čr, česko, slovensko">
    <meta name="description" content="Všechny larpy na jednom místě. Larpová databáze.">
    <meta property="og:image" content="img/logo512.png" />
    <!-- Styles -->
    <link rel="stylesheet" type="text/css" href="templates/css/style.css"/>
    <link rel="stylesheet" type="text/css" href="templates/css/nivo-slider.css"/>
    <link rel="stylesheet" type="text/css" href="templates/css/smoothness/jquery-ui-1.8.24.custom.css"/>
    <!-- JavaScripts -->
    <script type="text/javascript" src="templates/js/prototype.js"></script>
    <script type="text/javascript" src="templates/js/md5.js"></script>
    <script type="text/javascript" src="templates/js/csld.js"></script>
    <script type="text/javascript" src="templates/js/jquery-1.8.3.js"></script>
    <script type="text/javascript" src="templates/js/jquery-ui-1.9.2.custom.js"></script>
    <script type="text/javascript" src="templates/js/jquery.hint.js"></script>
    <script type="text/javascript" src="templates/js/balda_functions.js"></script>
    <script type="text/javascript" src="templates/js/jquery.nivo.slider.pack.js"></script>
    <script type="text/javascript" src="templates/js/checkboxes.js"></script>
    <script type="text/javascript">
        $(function(){
            // find all the input elements with title attributes - hinter fn to textbox
            $('input[title!=""]').hint();
        });
        /* $(document).ready(function() { // funkce pro spuštění Slideru
         $('#slider').s3Slider({
         timeOut: 3000
         });
         }); */
        jQuery(window).load(function(){
            jQuery("#nivoSlider").nivoSlider({
                effect:'boxRainGrow',
                slices:15,
                boxCols:8,
                boxRows:4,
                animSpeed:500,
                pauseTime:5000,
                startSlide:0,
                directionNav:true,
                controlNav:false,
                controlNavThumbs:false,
                pauseOnHover:false,
                manualAdvance:false
            });
        });

    </script>
</head>

<body>
<div id="fb-root"></div>
<script>
    // Additional JS functions here
    window.fbAsyncInit = function() {
        FB.init({
            appId      : '550966391596212', // App ID
            channelUrl : '//www.csldb.cz:8080/channel.html', // Channel File
            status     : true, // check login status
            cookie     : true, // enable cookies to allow the server to access the session
            xfbml      : true  // parse XFBML
        });

        // Additional init code here
        FB.getLoginStatus(function(response) {
            if (response.status === 'connected') {
                console.log('connected');
                // connected
                testAPI();
            } else if (response.status === 'not_authorized') {
                console.log('not authorized');
                // not_authorized
                login();
            } else {
                console.log('not logged');
                // not_logged_in
                login();
            }
        });
    };

    function login() {
        FB.login(function(response) {
            if (response.authResponse) {
                testAPI();
                // connected
            } else {
                // cancelled
            }
        });
    }

    function testAPI() {
        console.log('Welcome!  Fetching your information.... ');
        FB.api('/me', function(response) {
            console.log('Good to see you, ' + response.name + '.');
            console.log(response);
        });
    }

    (function(d){
        var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
        if (d.getElementById(id)) {return;}
        js = d.createElement('script'); js.id = id; js.async = true;
        js.src = "//connect.facebook.net/en_US/all.js";
        ref.parentNode.insertBefore(js, ref);
    }(document));
</script>
<div id="loginBox">
    <%@include file="/user/login.jsp"%>
</div>
<div id="box">
    <div id="header">
        <h1>www.csld.cz</h1>

        <h2>česko-slovenská larpová databáze</h2>
    </div>