if (this.FileUploadUI === undefined) {
    this.FileUploadUI = (function(window) {
        'use strict';

        var FileUploadUI = function(componentMarkupId, url, paramName, maxUploadSize, acceptedTypes, doneURL) {
            // Init upload
            var bar = $('#'+componentMarkupId);
            bar.fileupload({
                url: url,
                paramName: paramName,
                sequentialUploads: true,
                maxFileSize: maxUploadSize,
                acceptFileTypes: acceptedTypes
            });

            var DRAG_OVER_CLASS = "dragOver";

            // Done event postback
            bar.bind('fileuploadstop', function() {
                Wicket.Ajax.get({ u: doneURL });
            });

            // Error
            bar.bind('fileuploadfail', function() {
                alert('Chyba při nahrávání');
            });

            // Start callback
            bar.bind('fileuploadstart', function() {
                // Show progress bar
                bar.find('.uploadCover').show();

                // Remove drag class
                bar.removeClass(DRAG_OVER_CLASS);
            });

            // Progress updating
            bar.bind('fileuploadprogressall', function(e, data) {
                bar.find('.progressBar').css('width', parseInt(data.loaded / data.total * 100, 10)+'%');
            });

            // Drag over
            bar.bind('fileuploaddragover', function() {
                bar.addClass(DRAG_OVER_CLASS);
            });

            // Drag leave
            $(document).bind('dragleave', function() {
                bar.removeClass(DRAG_OVER_CLASS);
            });

            // Forward button click
            bar.find('input[type=button]').bind('click', function() {
                bar.find('input[type=file]').click();
            });
        }

        FileUploadUI.prototype.numUploads = 0;

        return FileUploadUI;
    }(this));
}
