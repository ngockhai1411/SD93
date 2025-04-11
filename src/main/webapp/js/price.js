$(document).ready(function() {

    $("#min_price,#max_price").on('change', function() {

        $('#price-range-submit').show();

        var min_price_range = parseInt($("#min_price").val());

        var max_price_range = parseInt($("#max_price").val());

        if (min_price_range > max_price_range) {
            $('#max_price').val(min_price_range);
        }

        $("#slider-range").slider({
            values: [min_price_range, max_price_range]
        });

    });


    $("#min_price,#max_price").on("paste keyup", function() {

        $('#price-range-submit').show();

        var min_price_range = parseInt($("#min_price").val());

        var max_price_range = parseInt($("#max_price").val());

        if (min_price_range == max_price_range) {

            max_price_range = min_price_range + 100;

            $("#min_price").val(min_price_range);
            $("#max_price").val(max_price_range);
        }

        $("#slider-range").slider({
            values: [min_price_range, max_price_range]
        });

    });


    $(function() {
        $("#slider-range").slider({
            range: true,
            orientation: "horizontal",
            min: 0,
            max: 3000000,
            values: [0, 900000],
            step: 300000,

            slide: function(event, ui) {
                if (ui.values[0] == ui.values[1]) {
                    return false;
                }

                $("#min_price").val(ui.values[0]);
                $("#max_price").val(ui.values[1]);
            }
        });

        $("#min_price").val($("#slider-range").slider("values", 0));
        $("#max_price").val($("#slider-range").slider("values", 1));

    });


});


$(document).ready(function() {


    $("#min_price_mobile,#max_price_mobile").on('change', function() {

        var min_price_range = parseInt($("#min_price_mobile").val());

        var max_price_range = parseInt($("#max_price_mobile").val());

        if (min_price_range > max_price_range) {
            $('#max_price_mobile').val(min_price_range);
        }

        $("#slider-range_mobile").slider({
            values: [min_price_range, max_price_range]
        });

    });


    $("#min_price_mobile,#max_price_mobile").on("paste keyup", function() {

        var min_price_range = parseInt($("#min_price_mobile").val());

        var max_price_range = parseInt($("#max_price_mobile").val());

        if (min_price_range == max_price_range) {

            max_price_range = min_price_range + 100;

            $("#min_price_mobile").val(min_price_range);
            $("#max_price_mobile").val(max_price_range);
        }

        $("#slider-range-mobile").slider({
            values: [min_price_range, max_price_range]
        });

    });


    $(function() {
        $("#slider-range-mobile").slider({
            range: true,
            orientation: "horizontal",
            min: 0,
            max: 3000000,
            values: [0, 300000],
            step: 300000,

            slide: function(event, ui) {
                if (ui.values[0] == ui.values[1]) {
                    return false;
                }

                $("#min_price_mobile").val(ui.values[0]);
                $("#max_price_mobile").val(ui.values[1]);
            }
        });

        $("#min_price_mobile").val($("#slider-range-mobile").slider("values", 0));
        $("#max_price_mobile").val($("#slider-range-mobile").slider("values", 1));

    });


});