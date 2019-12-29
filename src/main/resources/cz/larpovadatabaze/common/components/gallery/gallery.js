if (this.CSLDGallery === undefined) {
    this.CSLDGallery = (function(window) {
        'use strict';

        var CSLDGallery = function(componentId, imageURL, actionURL, deleteConfirmQuestion, data) {
            this._componentId = componentId;
            this._$gallery = $('#'+componentId);
            this._$display = this._$gallery.find('.display');
            this._$descriptionText = this._$gallery.find('.description');
            this._$descriptionTextArea = this._$gallery.find('textarea[name=description]');
            this._$orderText = this._$gallery.find('.orderText');
            this._imageURL = imageURL;
            this._actionURL = actionURL;
            this._deleteConfirmQuestion = deleteConfirmQuestion;
            this._displayWidth = this._$display.innerWidth();
            this._displayHeight = this._$display.innerHeight();
            this._$pagingPrev = this._$gallery.find('.paging.left');
            this._$pagingNext = this._$gallery.find('.paging.right');
            this._$btnModify = this._$gallery.find('input[name=modify]');
            this._$btnDelete = this._$gallery.find('input[name=delete]');
            this._$btnSave = this._$gallery.find('input[name=save]');
            this._$btnCancel = this._$gallery.find('input[name=cancel]');
            this._$btnOrder = this._$gallery.find('input[name=order]');
            this._$btnPublish = this._$gallery.find('input[name=publish]');
            this._$btnHide = this._$gallery.find('input[name=unpublish]');

            var wantedId = window.CSLDGallery.selectedId[componentId];
            var itemToSelect = null;

            // Create preview components
            this._$list = this._$gallery.find('ul');
            for(var i=0;i<data.length;i++) {
                var d = data[i];

                var l = $('<li></li>');
                $('<img />').attr('src', this._imageURL+'?id='+ d.id).appendTo(l);
                l.data(this._DATA_KEY, d).appendTo(this._$list);

                l.click(function(e) { this._selectItem($(e.target).parents('li')); }.bind(this));

                // Should we select this item
                if (itemToSelect == null) itemToSelect = l; // Select first
                else {
                    // Check if we got the right id
                    if (d.id === wantedId) itemToSelect = l; // This is the item to select
                }
            }

            if (data.length == 0) this._$gallery.hide(); // Hide gallery on no data
            else this._selectItem(itemToSelect, true); // Select item

            // Paging
            this._$pagingPrev.click(this._selectPrev.bind(this));
            this._$pagingNext.click(this._selectNext.bind(this));
            this._$gallery.keyup(function(e) {
                if (this._keysEnabled) {
                    if (e.keyCode == 37) this._selectPrev();
                    if (e.keyCode == 39) this._selectNext();
                }
            }.bind(this));

            // Buttons
            this._$btnModify.click(this._actionModify.bind(this));
            this._$btnDelete.click(this._actionDelete.bind(this));
            this._$btnSave.click(this._actionSave.bind(this));
            this._$btnCancel.click(this._actionCancel.bind(this));
            this._$btnOrder.click(this._actionOrder.bind(this));
            this._$btnPublish.click(this._actionPublish.bind(this));
            this._$btnHide.click(this._actionHide.bind(this));
        }


        /**
         * Constants
         */
        CSLDGallery.prototype._DATA_KEY = "gimg";
        CSLDGallery.prototype._ACTIVE_CLASS = "selected";

        /**
         * Global array of selected ID, kay is element id, value is selected image id. Used to show the same image after reinit.
         */
        CSLDGallery.selectedId = {};

        /**
         * Gallery root
         */
        CSLDGallery.prototype._$gallery = null;

        /**
         * Full image display
         */
        CSLDGallery.prototype._$display = null;

        /**
         * Description text div
         */
        CSLDGallery.prototype._$descriptionText = null;

        /**
         * Order text div
         */
        CSLDGallery.prototype._$orderText = null;

        /**
         * Description textarea
         */
        CSLDGallery.prototype._$descriptionTextArea = null;

        /**
         * Current list item
         */
        CSLDGallery.prototype._$current = null;

        /**
         * Paging prev
         */
        CSLDGallery.prototype._$pagingPrev = null;

        /**
         * Paging next
         */
        CSLDGallery.prototype._$pagingNext = null;

        /**
         * "Modify" button
         */
        CSLDGallery.prototype._$btnModify = null;

        /**
         * "Delete" button
         */
        CSLDGallery.prototype._$btnDelete = null;

        /**
         * "Save" button
         */
        CSLDGallery.prototype._$btnSave = null;

        /**
         * "Cancel" button
         */
        CSLDGallery.prototype._$btnCancel = null;

        /**
         * "Order" button
         */
        CSLDGallery.prototype._$btnOrder = null;

        /**
         * List of images
         */
        CSLDGallery.prototype._$list = null;

        /**
         * URL to get images from
         */
        CSLDGallery.prototype._imageURL = null;

        /**
         * Question to ask when deleting
         */
        CSLDGallery.prototype._deleteConfirmQuestion = null;

        /**
         * URL to execute actions
         */
        CSLDGallery.prototype._actionURL = null;

        /**
         * Component id for this instance
         */
        CSLDGallery.prototype._componentId = null;

        /**
         * Width of display div
         */
        CSLDGallery.prototype._displayWidth = null;

        /**
         * Height of display div
         */
        CSLDGallery.prototype._displayHeight = null;

        /**
         * Control by keys is enabled
         */
        CSLDGallery.prototype._keysEnabled = true;

        /**
         * We are ordering imahes
         */
        CSLDGallery.prototype._orderingImages = false;

        /**
         * Selecvt
         * @param $listItem jQuery object with the list item
         * @private
         */
        CSLDGallery.prototype._selectItem = function($listItem, dontFocus) {
            // Clear last list item
            if (this._$current != null) {
                this._$current.removeClass(this._ACTIVE_CLASS);
            }

            // Set new item
            this._$current = $listItem;
            this._$current.addClass(this._ACTIVE_CLASS);

            // Get data
            var d = this._$current.data(this._DATA_KEY);

            // Remove old image
            this._$display.find('img').fadeOut(500, function() { $(this).remove() });

            // Compute image widths and position
            var mt,ml,w,h;
            var wr = d.width/this._displayWidth;
            var hr = d.height/this._displayHeight;
            if (wr > hr) {
                // Constrained by width
                w = this._displayWidth;
                h = Math.round(d.height / wr);
                mt = Math.round((this._displayHeight-h)/2);
                ml = 0;
            }
            else {
                // Constrained by height
                w = Math.round(d.width / hr);
                h = this._displayHeight;
                mt = 0;
                ml = Math.round((this._displayWidth-w)/2);
            }

            // Add new image
            var img = $('<img />').attr('src', this._imageURL+'?full=1&id='+ d.id);
            img.css('width', w+'px').css('height', h+'px').css('top',mt+'px').css('left', ml+'px');
            img.prependTo(this._$display);

            // Set description
            this._$descriptionText.text((d.desc==undefined)?"":d.desc);

            // Affect prev/next paging visibility
            this._setPagingVisibility();

            // Reset buttons
            if (!this._orderingImages) this._resetEditingUI(dontFocus);

            // Store selected id
            window.CSLDGallery.selectedId[this._componentId] = d.id;
        }

        /**
         * Set visibility of pagers based on whether prev/next item exist
         */
        CSLDGallery.prototype._setPagingVisibility = function() {
            if (this._$current.prev().length == 0) this._$pagingPrev.hide();
            else this._$pagingPrev.show();
            if (this._$current.next().length == 0) this._$pagingNext.hide();
            else this._$pagingNext.show();
        }

        /**
         * Select next item (if present)
         */
        CSLDGallery.prototype._selectNext = function() {
            var next = this._$current.next();
            if (next.length > 0) this._selectItem(next);
        }

        /**
         * Select previous item (if present)
         */
        CSLDGallery.prototype._selectPrev = function() {
            var prev = this._$current.prev();
            if (prev.length > 0) this._selectItem(prev);
        }

        /**
         * Reset editing UI
         */
        CSLDGallery.prototype._resetEditingUI = function(dontFocus) {
            // Hide and show
            if (this._actionURL == null) {
                // Read-only mode, hide everything
                if (this._$btnModify.val() != '') {
                    // We need it to be focusable
                    this._$btnModify.val('').wrap($('<div />').css('width', '0').css('overflow', 'hidden'));
                }
                this._$btnDelete.hide();
                this._$btnOrder.hide();
            }
            else {
                this._$descriptionText.show();
                this._$btnModify.show();
                this._$btnDelete.show();
                this._$btnOrder.show();
            }
            this._$descriptionTextArea.hide();
            this._$btnSave.hide();
            this._$btnCancel.hide();

            this._keysEnabled = true;
            if (!dontFocus) this._$btnModify.focus();
        }

        /**
         * Cancel editing and show
         */
        CSLDGallery.prototype._actionCancel = function() {
            this._resetEditingUI();
        }

        /**
         * Start editing description
         */
        CSLDGallery.prototype._actionModify = function() {
            // Copy text to textarea
            this._$descriptionTextArea.val(this._$descriptionText.text());

            // Hide and show
            this._$descriptionText.hide();
            this._$descriptionTextArea.show();
            this._$btnModify.hide();
            this._$btnDelete.hide();
            this._$btnOrder.hide();
            this._$btnSave.show();
            this._$btnCancel.show();

            // Focus textarea
            this._$descriptionTextArea.focus();

            // Disable control by keys
            this._keysEnabled = false;
        }

        /**
         * Save new description
         */
        CSLDGallery.prototype._actionSave = function() {
            if (this._orderingImages) {
                /* Save image order */

                // Build list of ids in this order
                var ids = [];
                this._$list.find('li').each(function(idx, elm) {
                    ids.push($(elm).data(this._DATA_KEY).id);
                }.bind(this));

                // Execute ajax
                Wicket.Ajax.post({ u: this._actionURL, ep: {
                    "action": 'setOrder',
                    "imageIds": ids.join(",")
                }});
            }
            else {
                // Save new description
                var newDescription = this._$descriptionTextArea.val();
                var d = this._$current.data(this._DATA_KEY);
                Wicket.Ajax.post({ u: this._actionURL, ep: {
                    "action": 'update',
                    "imageId": d.id,
                    "newDescription": newDescription
                }});

                // Set to text and reset UI
                d.desc = newDescription;
                this._$descriptionText.text(newDescription);
                this._resetEditingUI();
            }
        }

        /**
         * Publish this photo for showing on the front page.
         * @private
         */
        CSLDGallery.prototype._actionPublish = function() {
            var d = this._$current.data(this._DATA_KEY);
            Wicket.Ajax.post({ u: this._actionURL, ep: {
                "action": 'publish',
                "imageId": d.id
            }});
        }

        /**
         * Hide this photo from the homePage
         *
         * @private
         */
        CSLDGallery.prototype._actionHide = function() {
            var d = this._$current.data(this._DATA_KEY);
            Wicket.Ajax.post({ u: this._actionURL, ep: {
                "action": 'hide',
                "imageId": d.id
            }});
        }

        /**
         * Delete image
         */
        CSLDGallery.prototype._actionDelete = function() {
            // Delete
            if (confirm(this._deleteConfirmQuestion)) {
                // Send ajax
                Wicket.Ajax.post({ u: this._actionURL, ep: {
                    "action": 'delete',
                    "imageId": this._$current.data(this._DATA_KEY).id
                }});

                // Select another photo
                var toSelect = this._$current.next(); // Try next image
                if (toSelect.length == 0) toSelect = this._$current.prev(); // Try prev image
                if (toSelect.length == 0) toSelect = null; // Deleted last image

                this._$current.remove(); // Delete preview image element
                this._$current = null;

                // Select new photo
                if (toSelect == null) this._$gallery.hide(); // Hide gallery on no data
                else this._selectItem(toSelect);
            }
        }

        /**
         * Set up order of photos
         */
        CSLDGallery.prototype._actionOrder = function() {
            this._$btnModify.hide();
            this._$btnDelete.hide();
            this._$btnOrder.hide();
            this._$btnSave.show();
            this._$orderText.show();

            this._orderingImages = true;

            // Init sortable
            this._$list.sortable({
                distance: 10,
                stop: this._setPagingVisibility.bind(this)
            });
        }

        return CSLDGallery;
    }(this));
}


