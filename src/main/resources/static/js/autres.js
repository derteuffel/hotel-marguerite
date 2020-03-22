
var autres = [
    "Savon le coq",
    "Savon en poudre",
    "Esprtit de sel",
    "Detergent bacterole",
    "Savon de toilette",
    "Papier serviette",
    "Vin",
    "Raclette",
    "Brosse",
    "Lave vitre",
    "Soumamousse",
    "Javel",
    "Vim",
    "Papier Hygienique",
    "Frigo",
    "Télévision",
    "Interphone",
    "Chaise de luxe",
    "Chaise bralima",
    "Table bralima",
    "Congelateur",
];
var lingeries = [
    "MATELA",
    "SERVIETTES",
    "BLOUSE",
    "Drap Blanc",
    "Couverture",
    "Drap multicolore",
    "Moustiquaire",
];



var chambres = [
    "CHAMBRE 101",
    "CHAMBRE 102",
    "CHAMBRE 103",
    "CHAMBRE 104",
    "CHAMBRE 105",
    "CHAMBRE 106",
    "CHAMBRE 107",
    "CHAMBRE 108",
    "CHAMBRE 109",
    "CHAMBRE 110",
    "CHAMBRE 211",
    "CHAMBRE 212",
    "CHAMBRE 213",
    "CHAMBRE 214",
    "CHAMBRE 215",
    "CHAMBRE 216",
    "CHAMBRE 217",
    "CHAMBRE 218",
    "CHAMBRE 219",
    "CHAMBRE 220",
    "CHAMBRE 321",
    "CHAMBRE 322",
    "CHAMBRE 323",
    "CHAMBRE 324",
    "CHAMBRE 325",
    "CHAMBRE 326",
    "CHAMBRE 327",
    "CHAMBRE 328",
    "CHAMBRE 329",
    "CHAMBRE 330",
    "CHAMBRE 431",
    "CHAMBRE 432",
    "CHAMBRE 433",
    "CHAMBRE 434",
    "CHAMBRE 435",
    "CHAMBRE 436",
    "CHAMBRE 437",
    "CHAMBRE 438",
    "CHAMBRE 439",
    "CHAMBRE 440",
    "CHAMBRE 541",
    "CHAMBRE 542",
    "CHAMBRE 543",
    "CHAMBRE 544",
    "CHAMBRE 545",
    "CHAMBRE 546",
    
];

function autocomplete(inp, arr) {
    /*the autocomplete function takes two arguments,
    the text field element and an array of possible autocompleted values:*/
    var currentFocus;
    /*execute a function when someone writes in the text field:*/
    inp.addEventListener("input", function(e) {
        var a, b, i, val = this.value;
        /*close any already open lists of autocompleted values*/
        closeAllLists();
        if (!val) { return false;}
        currentFocus = -1;
        /*create a DIV element that will contain the items (values):*/
        a = document.createElement("DIV");
        a.setAttribute("id", this.id + "autocomplete-list");
        a.setAttribute("class", "autocomplete-items");
        /*append the DIV element as a child of the autocomplete container:*/
        this.parentNode.appendChild(a);
        /*for each item in the array...*/
        for (i = 0; i < arr.length; i++) {
            /*check if the item starts with the same letters as the text field value:*/
            if (arr[i].substr(0, val.length).toUpperCase() == val.toUpperCase()) {
                /*create a DIV element for each matching element:*/
                b = document.createElement("DIV");
                /*make the matching letters bold:*/
                b.innerHTML = "<strong>" + arr[i].substr(0, val.length) + "</strong>";
                b.innerHTML += arr[i].substr(val.length);
                /*insert a input field that will hold the current array item's value:*/
                b.innerHTML += "<input type='hidden' value='" + arr[i] + "'>";
                /*execute a function when someone clicks on the item value (DIV element):*/
                b.addEventListener("click", function(e) {
                    /*insert the value for the autocomplete text field:*/
                    inp.value = this.getElementsByTagName("input")[0].value;
                    /*close the list of autocompleted values,
                    (or any other open lists of autocompleted values:*/
                    closeAllLists();
                });
                a.appendChild(b);
            }
        }
    });
    /*execute a function presses a key on the keyboard:*/
    inp.addEventListener("keydown", function(e) {
        var x = document.getElementById(this.id + "autocomplete-list");
        if (x) x = x.getElementsByTagName("div");
        if (e.keyCode == 40) {
            /*If the arrow DOWN key is pressed,
            increase the currentFocus variable:*/
            currentFocus++;
            /*and and make the current item more visible:*/
            addActive(x);
        } else if (e.keyCode == 38) { //up
            /*If the arrow UP key is pressed,
            decrease the currentFocus variable:*/
            currentFocus--;
            /*and and make the current item more visible:*/
            addActive(x);
        } else if (e.keyCode == 13) {
            /*If the ENTER key is pressed, prevent the form from being submitted,*/
            e.preventDefault();
            if (currentFocus > -1) {
                /*and simulate a click on the "active" item:*/
                if (x) x[currentFocus].click();
            }
        }
    });
    function addActive(x) {
        /*a function to classify an item as "active":*/
        if (!x) return false;
        /*start by removing the "active" class on all items:*/
        removeActive(x);
        if (currentFocus >= x.length) currentFocus = 0;
        if (currentFocus < 0) currentFocus = (x.length - 1);
        /*add class "autocomplete-active":*/
        x[currentFocus].classList.add("autocomplete-active");
    }
    function removeActive(x) {
        /*a function to remove the "active" class from all autocomplete items:*/
        for (var i = 0; i < x.length; i++) {
            x[i].classList.remove("autocomplete-active");
        }
    }
    function closeAllLists(elmnt) {
        /*close all autocomplete lists in the document,
        except the one passed as an argument:*/
        var x = document.getElementsByClassName("autocomplete-items");
        for (var i = 0; i < x.length; i++) {
            if (elmnt != x[i] && elmnt != inp) {
                x[i].parentNode.removeChild(x[i]);
            }
        }
    }
    /*execute a function when someone clicks in the document:*/
    document.addEventListener("click", function (e) {
        closeAllLists(e.target);
    });
}
autocomplete(document.getElementById("otherName"), autres);

