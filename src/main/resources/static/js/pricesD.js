


function checkValue(value,arr){
    var status = 'Not exist';

    for(var i=0; i<arr.length; i++){
        var name = arr[i];
        if(name == value){
            status = 'Exist';
            break;
        }
    }

    return status;
}


// article automatical changing prices
let selectedBoissonItem = $("#boissonName");
selectedBoissonItem.change(() => {
    if(selectedBoissonItem.val()== "MUTZIG pt")
    {

        $("#prixU").val("2500");
    }else if (selectedBoissonItem.val()== "MUTZIG class pt")
    {
        $("#prixU").val("2500");
    }else if (selectedBoissonItem.val()== "BEAUFORT Pt")
{
    $("#prixU").val("2500");
}else if (selectedBoissonItem.val()== "PRIMUS Pt")
{
    $("#prixU").val("2500");
}else if (selectedBoissonItem.val()== "TURBO King pt")
{
    $("#prixU").val("2500");
}else if (selectedBoissonItem.val()== "LEGENDE pt")
{
    $("#prixU").val("2500");
}else if (selectedBoissonItem.val()== "PRIMUS Gd")
{
    $("#prixU").val("3500");
}else if (selectedBoissonItem.val()== "SKOL Gd")
{
    $("#prixU").val("3500");
}else if (selectedBoissonItem.val()== "VICTOIRE")
{
    $("#prixU").val("3500");
}else if (selectedBoissonItem.val()== "NKOYI Gd")
{
    $("#prixU").val("3500");
}else if (selectedBoissonItem.val()== "NKOYI Rumba")
{
    $("#prixU").val("3500");
}else if (selectedBoissonItem.val()== "TEMBO")
{
    $("#prixU").val("3500");
}else if (selectedBoissonItem.val()== "33 EXPORT")
{
    $("#prixU").val("3500");
}else if (selectedBoissonItem.val()== "TURBO King")
{
    $("#prixU").val("3500");
}else if (selectedBoissonItem.val()== "CASTEL Gd")
{
    $("#prixU").val("3000");
}else if (selectedBoissonItem.val()== "HEINEKEN Btlle")
{
    $("#prixU").val("3000");
}else if (selectedBoissonItem.val()== "MUTZIG Gd")
{
    $("#prixU").val("3000");
}else if (selectedBoissonItem.val()== "MUTZIG class Gd")
{
    $("#prixU").val("3000");
}else if (selectedBoissonItem.val()== "DOPPEL")
{
    $("#prixU").val("3000");
}else if (selectedBoissonItem.val()== "TEMBO")
{
    $("#prixU").val("3000");
}else if (selectedBoissonItem.val()== "MUTZIG Bock")
{
    $("#prixU").val("3000");
}else if (selectedBoissonItem.val()== "Fanta petit")
{
    $("#prixU").val("1500");
}else if (selectedBoissonItem.val()== "XXL")
{
    $("#prixU").val("2000");
}else if (selectedBoissonItem.val()== "Fayrouz")
{
    $("#prixU").val("2000");
}else if (selectedBoissonItem.val()== "Coca-cola")
{
    $("#prixU").val("1500");
}else if (selectedBoissonItem.val()== "Coca-cola zero")
{
    $("#prixU").val("1500");
}else if (selectedBoissonItem.val()== "Top")
{
    $("#prixU").val("2000");
}else if (selectedBoissonItem.val()== "Malta")
{
    $("#prixU").val("2000");
}else if (selectedBoissonItem.val()== "Matina")
{
    $("#prixU").val("2000");
}else if (selectedBoissonItem.val()== "Tonic")
{
    $("#prixU").val("1500");
}else if (selectedBoissonItem.val()== "Schweppes soda")
{
    $("#prixU").val("1500");
}else if (selectedBoissonItem.val()== "Vital'o petit")
{
    $("#prixU").val("1500");
}else if (selectedBoissonItem.val()== "Sprite")
{
    $("#prixU").val("1500");
}else if (selectedBoissonItem.val()== "Eau plate ou eau vive Gd")
{
    $("#prixU").val("1000");
}else if (selectedBoissonItem.val()== "Eau plate ou eau vive Pt")
{
    $("#prixU").val("1000");
}else if (selectedBoissonItem.val()== "J&B")
{
    $("#prixU").val("25");
}else if (selectedBoissonItem.val()== "Jhonnie W, Red label")
{
    $("#prixU").val("25");
}else if (selectedBoissonItem.val()== "VODKA ABSOLUT")
{
    $("#prixU").val("25");
}else if (selectedBoissonItem.val()== "Drostdy Hof grand vin par 24")
{
    $("#prixU").val("30");
}else if (selectedBoissonItem.val()== "Nederburg WMAS Baronne par 24")
{
    $("#prixU").val("30");
}else if (selectedBoissonItem.val()== "Chemin de pape")
{
    $("#prixU").val("30");
}else if (selectedBoissonItem.val()== "Baron de Madrid")
{
    $("#prixU").val("30");
}else if (selectedBoissonItem.val()== "Bailly")
{
    $("#prixU").val("30");
}else if (selectedBoissonItem.val()== "Cires")
{
    $("#prixU").val("8000");
}else if (selectedBoissonItem.val()== "Don Simon")
{
    $("#prixU").val("8000");
}else if (selectedBoissonItem.val()== "Sangria")
{
    $("#prixU").val("8000");
}else if (selectedBoissonItem.val()== "Jhonnie W, Black label mesure")
{
    $("#prixU").val("8000");
}else if (selectedBoissonItem.val()== "Jack Daniels mesure")
{
    $("#prixU").val("8000");
}else if (selectedBoissonItem.val()== "VODKA GREY ROOSE mesure")
{
    $("#prixU").val("8000");
}else if (selectedBoissonItem.val()== "William Lawson")
{
    $("#prixU").val("15");
}else if (selectedBoissonItem.val()== "Jhonnie W, Black label")
{
    $("#prixU").val("40");
}else if (selectedBoissonItem.val()== "Jhonnie W, Black label")
{
    $("#prixU").val("40");
}
});



let selectedFoodItem = $("#foodName");
selectedFoodItem.change(()=>{
    if(selectedFoodItem.val() == "Poison Fumé + Fufu")
    {
        $("#food").val("15000");
    }else if(selectedFoodItem.val() == "Thomson grier au Makala")
{
    $("#food").val("15000");
}else if(selectedFoodItem.val() == "Poison Bitoyo")
{
    $("#food").val("15000");
}else if(selectedFoodItem.val() == "1/4 Poulet Acomp Frites")
{
    $("#food").val("10000");
}
else if(selectedFoodItem.val() == "Mitshop ya Ntaba")
{
    $("#food").val("10000");
}else if(selectedFoodItem.val() == "Poison Salé + Banane ou Frites")
{
    $("#food").val("12000");
}else if(selectedFoodItem.val() == "Ndakala + Fufu")
{
    $("#food").val("12000");
}
else if(selectedFoodItem.val() == "Sausuce + Frite")
{
    $("#food").val("12000");
}else if(selectedFoodItem.val() == "Jeune Capitaine + Banane")
{
    $("#food").val("20000");
}
else if(selectedFoodItem.val() == "Tilapia Frets + Frites ou Chikuange")
{
    $("#food").val("20000");
}else if(selectedFoodItem.val() == "Samussa 6 Pieces")
{
    $("#food").val("5");
}else if(selectedFoodItem.val() == "Croque Madame Pain Oeuf Lait Salade")
{
    $("#food").val("5");
}else if(selectedFoodItem.val() == "Tacos Boeuf")
{
    $("#food").val("5");
}
else if(selectedFoodItem.val() == "Crep Sucre")
{
    $("#food").val("5");
}else if(selectedFoodItem.val() == "Crep Sallé")
{
    $("#food").val("5");
}else if(selectedFoodItem.val() == "Salade Mixte")
{
    $("#food").val("5");
}
else if(selectedFoodItem.val() == "Salade Nichoise")
{
    $("#food").val("5");
}else if(selectedFoodItem.val() == "Salade de Fruit")
{
    $("#food").val("5");
}
else if(selectedFoodItem.val() == "Dejeune Complet")
{
    $("#food").val("5");
}else if(selectedFoodItem.val() == "Cossa AcmpFrites")
{
    $("#food").val("5");
}else if(selectedFoodItem.val() == "Dejeune Simple")
{
    $("#food").val("5000");
}else if(selectedFoodItem.val() == "Hambourger Simple")
{
    $("#food").val("5000");
}else if(selectedFoodItem.val() == "Pizza Bolonaise Viande")
{
    $("#food").val("20");
}else if(selectedFoodItem.val() == "Pizza Mare - Fruit de Mere")
{
    $("#food").val("20");
}
else if(selectedFoodItem.val() == "Pizza Mexicana-Saucise")
{
    $("#food").val("20");
}else if(selectedFoodItem.val() == "Pizza 4 Fraumage Viande/Poulet")
{
    $("#food").val("20");
}else if(selectedFoodItem.val() == "Pizza 4 Saison")
{
    $("#food").val("20");
}else if(selectedFoodItem.val() == "Plache Miche Samoussa: 1/4 de Poulet Frites Sauces")
{
    $("#food").val("20");
}else if(selectedFoodItem.val() == "Frites Simple")
{
    $("#food").val("4000");
}
else if(selectedFoodItem.val() == "Banane Simple")
{
    $("#food").val("4000");
}else if(selectedFoodItem.val() == "Sandwich")
{
    $("#food").val("4000");
}else if(selectedFoodItem.val() == "Brochette de Capitaine")
{
    $("#food").val("4000");
}else if(selectedFoodItem.val() == "Chawarma Poulet")
{
    $("#food").val("4000");
}else if(selectedFoodItem.val() == "Petit Poid Melangé avec Pomme de terre")
{
    $("#food").val("10");
}else if(selectedFoodItem.val() == "Porc + Frites, Banane ou Fufu")
{
    $("#food").val("10");
}else if(selectedFoodItem.val() == "Chevre + Frites ou Banane")
{
    $("#food").val("10");
}else if(selectedFoodItem.val() == "Haricot avec Riz")
{
    $("#food").val("8000");
}else if(selectedFoodItem.val() == "Steck")
{
    $("#food").val("8");
}else if(selectedFoodItem.val() == "Brochette de Boeuf")
{
    $("#food").val("3500");
}else if(selectedFoodItem.val() == "Brochette de Poulet")
{
    $("#food").val("3500");
}else if(selectedFoodItem.val() == "Poulet Entien Acomp Frites")
{
    $("#food").val("25000");
}else if(selectedFoodItem.val() == "Pizza Cappriciossa - Jambon")
{
    $("#food").val("20000");
}else if(selectedFoodItem.val() == "Pizza Marqueta Fraumage Basilique")
{
    $("#food").val("11000");
}else if(selectedFoodItem.val() == "Hambourger Tige")
{
    $("#food").val("6500");
}else if(selectedFoodItem.val() == "Tacos Poulet")
{
    $("#food").val("10");
}

});

